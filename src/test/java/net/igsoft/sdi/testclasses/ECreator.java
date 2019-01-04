package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class ECreator extends CreatorBase<EClass, LaunchType> {
    @Override
    public EClass create(InstanceProvider instanceProvider, LaunchType launchType) {
        return new EClass(instanceProvider.getOrCreate(Stepper.class));
    }
}
