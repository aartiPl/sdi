package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class ECreator extends CreatorBase<E, LaunchType> {
    @Override
    public E create(InstanceProvider instanceProvider, LaunchType launchType) {
        return new E(instanceProvider.getOrCreate(Stepper.class));
    }
}
