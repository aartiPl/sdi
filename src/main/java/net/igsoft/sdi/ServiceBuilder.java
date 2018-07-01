package net.igsoft.sdi;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import net.igsoft.sdi.internal.Instance;
import net.igsoft.sdi.internal.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ServiceBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBuilder.class);

    private final Map<Class<?>, Creator<?, ?>> creators = new HashMap<>();
    private final Map<Class<?>, ParametersBase> roots = new HashMap<>();

    public ServiceBuilder withRootClass(Class<?> clazz) {
        return withRootClass(clazz, LaunchType.AUTOMATIC);
    }

    public ServiceBuilder withRootClass(Class<?> clazz, ParametersBase parameters) {
        if (roots.containsKey(clazz)) {
            throw new IllegalArgumentException(format("Class %s already provided as a root class before.",
                    clazz.getSimpleName()));
        }

        roots.put(clazz, parameters);
        return this;
    }

    public ServiceBuilder withCreator(Creator<?, ?> creator) {
        Class<?> createdClass = creator.getCreatedClass();

        if (creators.containsKey(createdClass)) {
            throw new IllegalArgumentException(
                    format("Duplicated creator given in 'withCreator' method:\n%s (for class: %s)",
                            creator.getClass().getSimpleName(), createdClass.getSimpleName()));
        }

        creators.put(createdClass, creator);
        return this;
    }

    public Service build() {
        Map<Class<?>, Creator<?, ?>> defaultCreators = extractDefaultCreators(creators);
        InstanceCreator instanceCreator = new InstanceCreator(creators, defaultCreators, this::getInstanceKey);

        //TODO: root classes are the level 1 classes --- it is not really necessary to pass them explicitly
        //But is it really good? How to pass creator parameters? Is it clear what program does then?
        //Also it is not possible to test if the code is clean e.g. if all creators are used.
        //Additionally when I move below block down, then dependencies are not yet calculated
        for (Map.Entry<Class<?>, ParametersBase> entry : roots.entrySet()) {
            instanceCreator.getOrCreate(entry.getKey(), entry.getValue());
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

        LOGGER.info("\nDependencies by level:\n{}",
                LoggingUtils.dependenciesByLevel(instancesByLevel));

        LOGGER.info("\nRoot classes:\n{}", instancesByLevel.get(1));

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

                if (defaultCreators.containsKey(createdClass) && !creators.containsKey(createdClass)) {
                    throw new IllegalStateException(format("Found duplicated default creators (c1: %s, c2: %s)" +
                                    ", but no explicit creator for class '%s' was given.",
                            defaultCreator.getClass().getSimpleName(),
                            defaultCreators.get(createdClass).getClass().getSimpleName(),
                            createdClass.getSimpleName()));
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
