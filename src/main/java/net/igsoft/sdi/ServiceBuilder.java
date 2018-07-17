package net.igsoft.sdi;

import static java.lang.String.format;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
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

    private final Map<Class<?>, Specification> specificationMap = new HashMap<>();
    private final Map<Class<?>, Specification> rootSpecificationMap = new HashMap<>();

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
                this.specificationMap.computeIfAbsent(createdClass, s -> new Specification());

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
            rootSpecificationMap.put(createdClass, specification);
        }

        return this;
    }

    public Service build() {
        Deque<Specification> stack = new ArrayDeque<>(specificationMap.values());

        while (!stack.isEmpty()) {
            Map<Class<?>, Specification> discoveredSpecs = getDefaultCreators(stack.poll());
            stack.addAll(discoveredSpecs.values());
            specificationMap.putAll(discoveredSpecs);
        }

        InstanceCreator instanceCreator =
                new InstanceCreator(specificationMap, this::getInstanceKey);

        Map<Class<?>, Specification> instancesToInitiate =
                rootSpecificationMap.isEmpty() ? specificationMap : rootSpecificationMap;

        for (Map.Entry<Class<?>, Specification> entry : instancesToInitiate.entrySet()) {
            if (!rootSpecificationMap.isEmpty() ||
                entry.getValue()
                     .getCreator()
                     .getParameterClass()
                     .isAssignableFrom(entry.getValue().getDefaultParameter().getClass())) {
                instanceCreator.getOrCreate(entry.getKey(), entry.getValue().getDefaultParameter());
            }
        }

        Multimap<Integer, String> instancesByLevel = TreeMultimap.create();

        for (Map.Entry<String, Specification> entry : instanceCreator.getRuntimeSpecificationMap()
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
                    LoggingUtils.dependenciesByClass(instanceCreator.getRuntimeSpecificationMap()));

        if (!instanceCreator.getUnusedCreators().isEmpty()) {
            LOGGER.warn("\nSome creators were not used during service construction. " +
                        "Consider removing them from creator list:\n{}",
                        LoggingUtils.unusedCreators(instanceCreator.getUnusedCreators()));
        }

        return new Service(this::getInstanceKey, instanceCreator.getInstances(), sortedLevels);
    }

    private Map<Class<?>, Specification> getDefaultCreators(Specification spec) {
        Map<Class<?>, Specification> discoveredSpecs = new HashMap<>();

        for (Creator<?, ?> defaultCreator : spec.getEffectiveCreator().defaultCreators()) {
            Class<?> createdClass = defaultCreator.getCreatedClass();

            if (!specificationMap.containsKey(createdClass)) {
                Specification createdSpecification = new Specification();
                createdSpecification.setDefaultCreator(defaultCreator);
                //TODO: passing default parameters for default creators
                createdSpecification.setDefaultParameter(LaunchType.AUTOMATIC);
                discoveredSpecs.put(createdClass, createdSpecification);
                continue;
            }

            //TODO: jeśli default creatory są te same, a także ich parametry są te same, to nie jest to problem
            if (specificationMap.get(createdClass).getDefaultCreator() != null &&
                specificationMap.get(createdClass).getCreator() == null) {
                throw new IllegalStateException(
                        format("Found duplicated default creators (c1: %s, c2: %s)" +
                               ", but no explicit creator for class '%s' was given.",
                               defaultCreator.getClass().getSimpleName(),
                               specificationMap.get(createdClass)
                                               .getDefaultCreator()
                                               .getClass()
                                               .getSimpleName(), createdClass.getSimpleName()));
            }
        }
        return discoveredSpecs;
    }

    private <T> String getInstanceKey(Class<T> clazz, String serializedParameters) {
        String id = clazz.getName();

        if (!serializedParameters.isEmpty()) {
            id += "_" + serializedParameters;
        }

        return id;
    }
}
