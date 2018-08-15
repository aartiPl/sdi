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

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.InstanceProvider;
import net.igsoft.sdi.utils.LoggingUtils;
import net.igsoft.sdi.internal.Specification;
import net.igsoft.sdi.parameter.LaunchType;
import net.igsoft.sdi.parameter.ParameterBase;

public class ServiceBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBuilder.class);

    private final Map<Class<?>, CreatorBase<?, ?>> creators = new HashMap<>();
    private final Map<Class<?>, CreatorBase<?, ?>> defaultCreators = new HashMap<>();
    private final Map<Class<?>, CreatorBase<?, ?>> rootCreators = new HashMap<>();
    private final Map<Class<?>, ParameterBase> defaultParameters = new HashMap<>();

    public ServiceBuilder withRootCreator(CreatorBase<?, ?> creator) {
        return withCreator(true, creator, null);
    }

    public <P extends ParameterBase> ServiceBuilder withRootCreator(CreatorBase<?, P> creator,
                                                                    P defaultParameter) {
        return withCreator(true, creator, defaultParameter);
    }

    public ServiceBuilder withCreator(CreatorBase<?, ?> creator) {
        return withCreator(false, creator, null);
    }

    public <P extends ParameterBase> ServiceBuilder withCreator(CreatorBase<?, P> creator,
                                                                P defaultParameter) {
        return withCreator(false, creator, defaultParameter);
    }

    private <P extends ParameterBase> ServiceBuilder withCreator(boolean rootCreator,
                                                                CreatorBase<?, P> creator,
                                                                P defaultParameter) {
        Class<?> createdClass = creator.getCreatedClass();

        if (creators.containsKey(createdClass)) {
            throw new IllegalArgumentException(
                    format("Duplicated creator given in 'withCreator' method:\n%s (for class: %s)",
                           creator.getClass().getSimpleName(), createdClass.getSimpleName()));
        }

        creators.put(createdClass, creator);

        if (rootCreator) {
            rootCreators.put(createdClass, creator);
        }

        if (defaultParameter != null) {
            defaultParameters.put(createdClass, defaultParameter);
        }

        return this;
    }

    public Service build() {
        Deque<CreatorBase<?, ?>> stack = new ArrayDeque<>(creators.values());

        while (!stack.isEmpty()) {
            Map<Class<?>, CreatorBase<?, ?>> discoveredDefaultCreators =
                    discoverDefaultCreators(stack.pop());
            stack.addAll(discoveredDefaultCreators.values());
        }

        InstanceProvider instanceProvider =
                new InstanceProvider(creators, defaultCreators, defaultParameters,
                                     this::getInstanceKey);

        Map<Class<?>, CreatorBase<?, ?>> instancesToInitiate =
                !rootCreators.isEmpty() ? rootCreators : creators;

        for (Map.Entry<Class<?>, CreatorBase<?, ?>> entry : instancesToInitiate.entrySet()) {
            Class<?> key = entry.getKey();

            if (defaultParameters.containsKey(key)) {
                instanceProvider.getOrCreate(key, defaultParameters.get(key));
            } else {
                throw new IllegalStateException(format("Creator '%s' (for class '%s') does not have " +
                                                       "a required parameter of type '%s'", entry.getValue().getClass().getSimpleName(),
                                                       key.getSimpleName(), entry.getValue().getParameterClass().getSimpleName()));
            }
        }

        Multimap<Integer, String> instancesByLevel = TreeMultimap.create();

        for (Map.Entry<String, Specification> entry : instanceProvider.getRuntimeSpecification()
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

        LOGGER.info("\nDependencies by class:\n{}",
                    LoggingUtils.dependenciesByClass(instanceProvider.getRuntimeSpecification()));

        LOGGER.info("\nRoot classes:\n{}", instancesByLevel.get(1));

        if (!instanceProvider.getUnusedCreators().isEmpty()) {
            LOGGER.warn("\nSome creators were not used during service construction. " +
                        "Consider removing them from creator list:\n{}",
                        LoggingUtils.unusedCreators(instanceProvider.getUnusedCreators()));
        }

        return new Service(this::getInstanceKey, instanceProvider.getInstances(), sortedLevels);
    }

    private Map<Class<?>, CreatorBase<?, ?>> discoverDefaultCreators(CreatorBase<?, ?> creator) {
        //Apply defaults
        if (defaultParameters.get(creator.getCreatedClass()) == null &&
            creator.getParameterClass().equals(LaunchType.class)) {
            defaultParameters.put(creator.getCreatedClass(), LaunchType.AUTOMATIC);
        }

        //Discover default creators
        Map<Class<?>, CreatorBase<?, ?>> discoveredCreators = new HashMap<>();
        for (CreatorBase<?, ?> defaultCreator : creator.defaultCreators()) {
            Class<?> createdClass = defaultCreator.getCreatedClass();

            if (!defaultCreators.containsKey(createdClass)) {
                defaultCreators.put(createdClass, defaultCreator);
                discoveredCreators.put(createdClass, defaultCreator);
                continue;
            }

            if (!defaultCreators.get(createdClass).getClass().equals(defaultCreator.getClass()) &&
                !creators.containsKey(createdClass)) {
                throw new IllegalStateException(
                        format("Found duplicated default creators (c1: %s, c2: %s)" +
                               ", but no explicit creator for class '%s' was given.",
                               defaultCreator.getClass().getSimpleName(),
                               defaultCreators.get(createdClass).getClass().getSimpleName(),
                               createdClass.getSimpleName()));
            }
        }
        return discoveredCreators;
    }

    private <T> String getInstanceKey(Class<T> clazz, String serializedParameters) {
        String id = clazz.getName();

        if (!serializedParameters.isEmpty()) {
            id += "_" + serializedParameters;
        }

        return id;
    }
}
