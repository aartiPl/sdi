package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import net.igsoft.sdi.internal.Instance;
import net.igsoft.sdi.internal.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBuilder.class);

    private final Map<Class<?>, Creator<?>> creators = new HashMap<>();

    private Class<?> mainClass;
    private CreatorParams params;
    private boolean manualStartAndStop;

    public ServiceBuilder withMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
        this.params = CreatorParams.EMPTY_PARAMS;
        this.manualStartAndStop = false;
        return this;
    }

    public ServiceBuilder withMainClass(Class<?> mainClass, CreatorParams params) {
        this.mainClass = mainClass;
        this.params = params;
        this.manualStartAndStop = false;
        return this;
    }

    public ServiceBuilder withMainClass(Class<?> mainClass, CreatorParams params,
                                        boolean manualStartAndStop) {
        this.mainClass = mainClass;
        this.params = params;
        this.manualStartAndStop = manualStartAndStop;
        return this;
    }

    public ServiceBuilder withCreator(Creator<?> creator) {
        Class<?> createdClass = getCreatedClass(creator);

        if (creators.containsKey(createdClass)) {
            throw new IllegalArgumentException(
                    "Duplicated creator given in 'withCreator' method:\n" +
                    creator.getClass().getSimpleName() +
                    " (for class: " +
                    createdClass.getSimpleName() +
                    ")");
        }

        creators.put(createdClass, creator);
        return this;
    }

    public Service build() {
        checkNotNull(mainClass);

        Map<Class<?>, Creator<?>> defaultCreators = extractDefaultCreators(creators);
        InstanceCreator instanceCreator = new InstanceCreator(creators, defaultCreators,
                                                              this::getInstanceKey);
        instanceCreator.getOrCreate(mainClass, params, manualStartAndStop);

        Multimap<Integer, String> instancesByLevel = TreeMultimap.create();

        for (Map.Entry<String, Instance> entry : instanceCreator.getInstances().entrySet()) {
            instancesByLevel.put(entry.getValue().getLevel(), entry.getKey());
        }

        LOGGER.info("\nDependencies by level:\n{}",
                    LoggingUtils.dependenciesByLevel(instancesByLevel));

        List<Collection<String>> sortedLevels = instancesByLevel.asMap()
                                                                .keySet()
                                                                .stream()
                                                                .sorted()
                                                                .map(instancesByLevel::get)
                                                                .collect(Collectors.toList());

        LOGGER.info("\nDependencies by class:\n{}",
                    LoggingUtils.dependenciesByClass(instanceCreator.getInstances()));

        if (!instanceCreator.getUnusedCreators().isEmpty()) {
            LOGGER.warn("\nSome creators were not used during service construction. " +
                        "Consider removing them from creator list:\n{}",
                        LoggingUtils.unusedCreators(instanceCreator.getUnusedCreators()));
        }

        return new Service(this::getInstanceKey, instanceCreator.getInstances(), sortedLevels);
    }

    private Map<Class<?>, Creator<?>> extractDefaultCreators(Map<Class<?>, Creator<?>> creators) {
        Map<Class<?>, Creator<?>> defaultCreators = new HashMap<>();

        for (Creator<?> creator : creators.values()) {
            for (Creator<?> defaultCreator : creator.defaultCreators()) {
                Class<?> createdClass = getCreatedClass(defaultCreator);

                if (defaultCreators.containsKey(createdClass) &&
                    !creators.containsKey(createdClass)) {
                    throw new IllegalStateException("Found duplicated default creators (c1: " +
                                                    defaultCreator.getClass().getSimpleName() +
                                                    ", c2: " +
                                                    defaultCreators.get(createdClass)
                                                                   .getClass()
                                                                   .getSimpleName() +
                                                    "), but no explicit creator for class '" +
                                                    createdClass.getSimpleName() +
                                                    "' was given.");
                }

                defaultCreators.put(createdClass, defaultCreator);
            }
        }

        return defaultCreators;
    }

    private <T> String getInstanceKey(Class<T> clazz, String serializedParameters) {
        String id = clazz.getName();

        if (!serializedParameters.isEmpty()) {
            id += "_" + serializedParameters;
        }

        return id;
    }

    private Class<?> getCreatedClass(Creator<?> creator) {
        String typeName = ((ParameterizedType) creator.getClass()
                                                      .getGenericSuperclass()).getActualTypeArguments()[0]
                .getTypeName();
        int typeParametersIndex = typeName.indexOf('<');

        if (typeParametersIndex != -1) {
            typeName = typeName.substring(0, typeParametersIndex);
        }

        Class<?> clazz;

        try {
            clazz = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can not find a Class for '" + typeName + "'.", e);
        }

        return clazz;
    }
}
