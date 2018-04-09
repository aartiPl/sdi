package net.igsoft.sdi;

import java.lang.reflect.Constructor;

public class AutoCreator<T> extends CreatorBase<T> {

    public AutoCreator(Class<T> myClazz) {
        super(myClazz);
    }

    @Override
    public T create(InstanceCreator instanceCreator) {
        Constructor<?>[] constructors = getCreatedClass().getConstructors();

        if (constructors.length > 1) {
            throw new IllegalStateException("Class '" + getCreatedClass().getSimpleName() + "' has more than one public " +
                    "constructor. Can not automatically construct these classes.");
        }

        if (constructors.length == 0 || constructors[0].getParameterCount() == 0) {
            T instance;
            try {
                instance = getCreatedClass().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("Can not automatically create class '" + getCreatedClass().getSimpleName() + "'",
                        e);
            }

            return instance;
        }

        @SuppressWarnings("unchecked")
        Constructor<T> constructor = (Constructor<T>) constructors[0];

        Object[] values = new Object[constructor.getParameterCount()];

        for (int i = 0; i < constructor.getParameterCount(); i++) {
            values[i] = instanceCreator.getOrCreate(constructor.getParameterTypes()[i]);
        }

        T instance;
        try {
            instance = constructor.newInstance(values);
        } catch (Exception e) {
            throw new IllegalStateException("Can not automatically create class '" + getCreatedClass().getSimpleName() + "'",
                    e);
        }

        return instance;
    }
}
