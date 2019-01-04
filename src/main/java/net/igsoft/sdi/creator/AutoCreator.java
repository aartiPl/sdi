package net.igsoft.sdi.creator;

import static java.lang.String.format;

import java.lang.reflect.Constructor;

import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;
import net.igsoft.sdi.parameter.ParameterBase;

public class AutoCreator<T, P extends ParameterBase> extends CreatorBase<T, P> {

    public AutoCreator(Class<T> myClazz) {
        super(myClazz, (Class<P>) LaunchType.class);
    }

    @Override
    public T create(InstanceProvider instanceProvider, P params) {

        if (!params.getClass().equals(LaunchType.class)) {
            throw new IllegalStateException(
                    "Can not automatically create instance based on creator parameters.");
        }

        Constructor<?>[] constructors = getCreatedClass().getConstructors();

        //Only single constructor is supported
        if (constructors.length > 1) {
            throw new IllegalStateException(
                    format("Class '%s' has more than one public constructor. Can not automatically create classes with more than one public constructors.",
                           getCreatedClass().getSimpleName()));
        }

        if (constructors.length == 0 || constructors[0].getParameterCount() == 0) {
            //Default constructor or single constructor without parameters
            T instance;
            try {
                instance = getCreatedClass().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(format("Can not automatically create class '%s'.",
                                                       getCreatedClass().getSimpleName()), e);
            }

            return instance;
        }

        //Single constructor with parameters
        @SuppressWarnings("unchecked")
        Constructor<T> constructor = (Constructor<T>) constructors[0];

        Object[] values = new Object[constructor.getParameterCount()];

        for (int i = 0; i < constructor.getParameterCount(); i++) {
            values[i] = instanceProvider.getOrCreate(constructor.getParameterTypes()[i]);
        }

        T instance;
        try {
            instance = constructor.newInstance(values);
        } catch (Exception e) {
            throw new IllegalStateException(format("Can not automatically create class '%s' with parameters.",
                                                   getCreatedClass().getSimpleName()), e);
        }

        return instance;
    }
}
