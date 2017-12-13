package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBuilder.class);
    private final List<Creator<?>> creators = new ArrayList<>();

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
        this.creators.add(creator);
        return this;
    }

    public Service build() {
        checkNotNull(mainClass);

        ImmutableMap.Builder<Class<?>, Creator<?>> creatorsBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Class<?>, Creator<?>> defaultCreatorsBuilder = ImmutableMap.builder();

        for (Creator<?> creator : creators) {
            creatorsBuilder.put(getCreatedClass(creator), creator);

            for (Creator<?> defaultCreator : creator.defaultCreators()) {
                defaultCreatorsBuilder.put(getCreatedClass(defaultCreator), defaultCreator);
            }
        }

        InstanceCreator instanceCreator = new InstanceCreator(creatorsBuilder.build(),
                                                              defaultCreatorsBuilder.build(),
                                                              this::getInstanceKey);
        return createService(instanceCreator, mainClass, params, manualStartAndStop);
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

    private Service createService(InstanceCreator instanceCreator, Class<?> mainClass,
                                  CreatorParams params, boolean manualStartAndStop) {
        instanceCreator.getOrCreate(mainClass, params, manualStartAndStop);

        Multimap<Integer, String> multimap = ArrayListMultimap.create();

        for (Map.Entry<String, Integer> entry : instanceCreator.getLevels().entrySet()) {
            multimap.put(entry.getValue(), entry.getKey());
        }

        StringBuilder sb;

        sb = new StringBuilder();
        for (Integer key : multimap.keySet()) {
            sb.append("Level ").append(key).append(": ").append(multimap.get(key)).append('\n');
        }
        LOGGER.info("Dependencies by level:\n{}", sb);

        List<Collection<String>> sortedLevels = multimap.asMap()
                                                        .keySet()
                                                        .stream()
                                                        .sorted()
                                                        .map(multimap::get)
                                                        .collect(Collectors.toList());

        sb = new StringBuilder();
        for (String key : instanceCreator.getDependencies().keySet()) {
            sb.append("Class '")
              .append(key)
              .append("': ")
              .append(instanceCreator.getDependencies().get(key))
              .append('\n');
        }
        LOGGER.info("Dependencies by class:\n{}", sb);

        if (!instanceCreator.getCreatorCheckMap().isEmpty()) {
            sb = new StringBuilder();
            for (Map.Entry<Class<?>, Creator<?>> entry : instanceCreator.getCreatorCheckMap()
                                                                        .entrySet()) {
                sb.append(entry.getValue().getClass().getSimpleName())
                  .append(" (for class: ")
                  .append(entry.getKey().getSimpleName())
                  .append(")\n");
            }

            LOGGER.warn("Some creators are not used during service construction. " +
                        "Consider removing them from creator list:\n{}", sb);
        }

        return new Service(instanceCreator, sortedLevels);
    }
}
