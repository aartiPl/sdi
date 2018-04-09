package net.igsoft.sdi;

public abstract class ParametrizedCreator<T, P> extends CreatorBase<T> {

    public T create(InstanceCreator instanceCreator, P params) {
        return null;
    }
}
