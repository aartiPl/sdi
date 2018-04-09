package net.igsoft.sdi;

public abstract class Creator<T> extends CreatorBase<T> {
    public abstract T create(InstanceCreator instanceCreator);
}
