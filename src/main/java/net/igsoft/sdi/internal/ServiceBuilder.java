package net.igsoft.sdi.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import net.igsoft.sdi.Creator;
import net.igsoft.sdi.CreatorParams;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.Service;

public class ServiceBuilder {

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

    public ServiceBuilder withMainClass(Class<?> mainClass, CreatorParams params, boolean manualStartAndStop) {
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

        InstanceCreator instanceCreator = new InstanceCreator(creatorsBuilder.build(), defaultCreatorsBuilder.build(), this::getInstanceKey);
        return instanceCreator.createService(mainClass, params, manualStartAndStop);
    }

    private <T> String getInstanceKey(Class<T> clazz, String serializedParameters) {
        String id = clazz.getName();

        if (!serializedParameters.isEmpty()) {
            id += "_" + serializedParameters;
        }

        return id;
    }

    private Class<?> getCreatedClass(Creator<?> creator) {
        String typeName = ((ParameterizedType) creator.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
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
