package net.igsoft.sdi;

import java.lang.reflect.Constructor;

public class AutoCreator<T, P extends ParametersBase> extends Creator<T, P> {

    public AutoCreator(Class<T> myClazz) {
        super(myClazz, (Class<P>)LaunchType.class);
    }

    @Override
    public T create(InstanceCreator instanceCreator, P params) {

        if (!params.getClass().equals(LaunchType.class)) {
            throw new IllegalStateException("Can not automatically create instances based on parameters passed to creator.");
        }

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
            //NOTE: startStopManually will be passed automatically down the stack
            values[i] = instanceCreator.getOrCreate(constructor.getParameterTypes()[i], params);
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