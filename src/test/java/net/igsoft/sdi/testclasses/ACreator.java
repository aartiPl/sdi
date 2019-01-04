package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class ACreator extends CreatorBase<AClass, LaunchType> {
    @Override
    public AClass create(InstanceProvider instanceProvider, LaunchType launchType) {
        BClass b = instanceProvider.getOrCreate(BClass.class, launchType);

        return new AClass(b, instanceProvider.getOrCreate(Stepper.class));
    }
}
