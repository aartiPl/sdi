package net.igsoft.sdi;

public abstract class ParametrizedCreator<P, R> extends CreatorBase<R> {

    public R create(InstanceCreator instanceCreator, P params) {
        return null;
    }

    public R create(InstanceCreator instanceCreator, CreatorParams params) {
        throw new UnsupportedOperationException(
                "Method 'T create(InstanceCreator instanceCreator, CreatorParams params)' must be implemented in creator of '" +
                        getCreatedClass().getName() + "'");
    }
}
