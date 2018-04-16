package net.igsoft.sdi;

public abstract class Creator<T> extends CreatorBase<T> {

    protected Creator() {
        super();
    }

    protected Creator(Class<T> myClazz) {
        super(myClazz);
    }

    public abstract T create(InstanceCreator instanceCreator);
}
