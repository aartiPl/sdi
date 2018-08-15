package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class ACreator extends CreatorBase<A, LaunchType> {
    @Override
    public A create(InstanceProvider instanceProvider, LaunchType launchType) {
        B b = instanceProvider.getOrCreate(B.class, launchType);

        return new A(b, instanceProvider.getOrCreate(Stepper.class));
    }
}
