package net.igsoft.sdi;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.google.common.collect.Lists;

//NOTE: This class has to be abstract class and not the interface to allow
//discovery of T class on runtime (if it is abstract class erasure is not deleting type of Creator).
public abstract class Creator<T, P extends ParameterBase> {

    private final Class<?> myClazz;
    private final Class<?> myParameter;

    public abstract T create(InstanceCreator instanceCreator, P params);

    protected Creator() {
        myClazz = getClassOfParentTypeParameter(this.getClass(), 0);
        myParameter = getClassOfParentTypeParameter(this.getClass(), 1);
    }

    protected Creator(Class<T> myClazz, Class<P> myParameter) {
        this.myClazz = myClazz;
        this.myParameter = myParameter;
    }

    public List<Creator<?, ?>> defaultCreators() {
        return Lists.newArrayList();
    }

    public Class<T> getCreatedClass() {
        return (Class<T>) myClazz;
    }

    public Class<P> getParameterClass() {
        return (Class<P>) myParameter;
    }

    private static Class<?> getClassOfParentTypeParameter(Class<?> derivedClazz,
                                                          int typeParameterIndex) {
        String typeName =
                ((ParameterizedType) derivedClazz.getGenericSuperclass()).getActualTypeArguments()[typeParameterIndex]
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
