package net.igsoft.sdi;

import com.google.common.collect.Lists;

import java.lang.reflect.ParameterizedType;
import java.util.List;

//NOTE: This class has to be abstract class and not the interface to allow
//discovery of T class on runtime (if it is abstract class erasure is not deleting type of Creator).
public abstract class Creator<T> {

    private final Class<?> myClazz = getClassOfParentTypeParameter(this.getClass(), 0);

    public T create(InstanceCreator instanceCreator) {
        throw new UnsupportedOperationException(
                "Method 'T create(InstanceCreator instanceCreator)' must be implemented in creator of '" + myClazz.getName() + "'");
    }

    public T create(InstanceCreator instanceCreator, CreatorParams params) {
        throw new UnsupportedOperationException(
                "Method 'T create(InstanceCreator instanceCreator, CreatorParams params)' must be implemented in creator of '" +
                        myClazz.getName() + "'");
    }

    public List<Creator<?>> defaultCreators() {
        return Lists.newArrayList();
    }

    private static Class<?> getClassOfParentTypeParameter(Class<?> derivedClazz, int typeParameterIndex) {
        String typeName = ((ParameterizedType) derivedClazz.getGenericSuperclass()).getActualTypeArguments()[typeParameterIndex]
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
