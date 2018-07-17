package net.igsoft.sdi;

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.igsoft.sdi.internal.LoggingUtils;
import net.igsoft.sdi.internal.Specification;

public class ServiceBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBuilder.class);

    private final Map<Class<?>, Specification> specification = new HashMap<>();
    private final Map<Class<?>, Specification> rootSpecification = new HashMap<>();

    public ServiceBuilder withCreator(Creator<?, ?> creator) {
        return withCreator(false, creator, null);
    }

    public <P extends ParameterBase> ServiceBuilder withCreator(Creator<?, P> creator,
                                                                P defaultParameter) {
        return withCreator(false, creator, defaultParameter);
    }

    public ServiceBuilder withRootCreator(Creator<?, ?> creator) {
        return withCreator(true, creator, null);
    }

    public <P extends ParameterBase> ServiceBuilder withRootCreator(Creator<?, P> creator,
                                                                    P defaultParameter) {
        return withCreator(true, creator, defaultParameter);
    }

    private <P extends ParameterBase> ServiceBuilder withCreator(boolean rootCreator,
                                                                 Creator<?, P> creator,
                                                                 P defaultParameter) {
        Class<?> createdClass = creator.getCreatedClass();

        Specification specification =
                this.specification.computeIfAbsent(createdClass, s -> new Specification());

        if (specification.getCreator() != null) {
            throw new IllegalArgumentException(
                    format("Duplicated creator given in 'withCreator' method:\n%s (for class: %s)",
                           creator.getClass().getSimpleName(), createdClass.getSimpleName()));
        }

        specification.setRootCreator(rootCreator);
        specification.setCreator(creator);
        specification.setDefaultParameter(
                defaultParameter == null ? LaunchType.AUTOMATIC : defaultParameter);

        if (rootCreator) {
            rootSpecification.put(createdClass, specification);
        }

        return this;
    }

    public Service build() {
        assignDefaultCreators(specification);
        InstanceCreator instanceCreator = new InstanceCreator(specification, this::getInstanceKey);

        //TODO: root classes are the level 1 classes --- it is not really necessary to pass them explicitly
        //But is it really good? How to pass creator parameters? Is it clear what program does then?
        //Also it is not possible to test if the code is clean e.g. if all creators are used.
        //Additionally when I move below block down, then dependencies are not yet calculated

        Map<Class<?>, Specification> instancesToInitiate =
                rootSpecification.isEmpty() ? specification : rootSpecification;

        for (Map.Entry<Class<?>, Specification> entry : instancesToInitiate.entrySet()) {
            instanceCreator.getOrCreate(entry.getKey(), entry.getValue().getDefaultParameter());
        }

        Multimap<Integer, String> instancesByLevel = TreeMultimap.create();

        for (Map.Entry<String, Specification> entry : instanceCreator.getDynamicSpecification()
                                                                     .entrySet()) {
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
                    LoggingUtils.dependenciesByClass(instanceCreator.getDynamicSpecification()));

        if (!instanceCreator.getUnusedCreators().isEmpty()) {
            LOGGER.warn("\nSome creators were not used during service construction. " +
                        "Consider removing them from creator list:\n{}",
                        LoggingUtils.unusedCreators(instanceCreator.getUnusedCreators()));
        }

        return new Service(this::getInstanceKey, instanceCreator.getInstances(), sortedLevels);
    }

    private void assignDefaultCreators(Map<Class<?>, Specification> instances) {
        for (Specification specification : instances.values()) {
            for (Creator<?, ?> defaultCreator : specification.getCreator().defaultCreators()) {
                Class<?> createdClass = defaultCreator.getCreatedClass();

                Specification createdSpecification =
                        instances.computeIfAbsent(createdClass, s -> new Specification());

                //TODO: jeśli default creatory są te same, to nie jest to problem
                if (createdSpecification.getDefaultCreator() != null &&
                    createdSpecification.getCreator() == null) {
                    throw new IllegalStateException(
                            format("Found duplicated default creators (c1: %s, c2: %s)" +
                                   ", but no explicit creator for class '%s' was given.",
                                   defaultCreator.getClass().getSimpleName(),
                                   createdSpecification.getDefaultCreator()
                                                       .getClass()
                                                       .getSimpleName(),
                                   createdClass.getSimpleName()));
                }

                createdSpecification.setDefaultCreator(defaultCreator);
            }
        }
    }

    private <T> String getInstanceKey(Class<T> clazz, String serializedParameters) {
        String id = clazz.getName();

        if (!serializedParameters.isEmpty()) {
            id += "_" + serializedParameters;
        }

        return id;
    }
}
