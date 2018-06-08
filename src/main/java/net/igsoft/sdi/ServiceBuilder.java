package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkNotNull;

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

    private final Map<Class<?>, Creator<?, ?>> creators = new HashMap<>();
    private final Map<Class<?>, ParametersBase> defaultParameters = new HashMap<>();

    public <P extends ParametersBase> ServiceBuilder withCreator(Creator<?, ?> creator) {
        withCreator(creator, LaunchType.AUTOMATIC);
        return this;
    }

    public <P extends ParametersBase> ServiceBuilder withCreator(Creator<?, ?> creator, P params) {
        Class<?> createdClass = creator.getCreatedClass();

        if (creators.containsKey(createdClass)) {
            throw new IllegalArgumentException(
                    "Duplicated creator given in 'withCreator' method:\n" +
                    creator.getClass().getSimpleName() +
                    " (for class: " +
                    createdClass.getSimpleName() +
                    ")");
        }

        creators.put(createdClass, creator);
        defaultParameters.put(createdClass, params);
        return this;
    }

    public Service build() {
        Map<Class<?>, Creator<?, ?>> defaultCreators = extractDefaultCreators(creators);
        InstanceCreator instanceCreator = new InstanceCreator(creators, defaultParameters, defaultCreators,
                                                              this::getInstanceKey);

        //FIXME: to tworzy wszytkie instancje dla domyślnych parametrów --- nie jest to lazy
        //niektóre instancje mogą być nadmiarowe
        for(Map.Entry<Class<?>, Creator<?, ?>> entry : creators.entrySet()) {

            //NOTE: we are building only instances with same parameter types
            if ((entry.getValue().getParameterClass()).equals(defaultParameters.get(entry.getKey()).getClass())) {
                instanceCreator.getOrCreate(entry.getKey(), defaultParameters.get(entry.getKey()));
            }
        }

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

    private Map<Class<?>, Creator<?, ?>> extractDefaultCreators(Map<Class<?>, Creator<?, ?>> creators) {
        Map<Class<?>, Creator<?, ?>> defaultCreators = new HashMap<>();

        for (Creator<?, ?> creator : creators.values()) {
            for (Creator<?, ?> defaultCreator : creator.defaultCreators()) {
                Class<?> createdClass = defaultCreator.getCreatedClass();

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
}
