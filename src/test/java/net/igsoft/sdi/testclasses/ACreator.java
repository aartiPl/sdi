package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class ACreator extends Creator<A, LaunchType> {
    @Override
    public A create(InstanceCreator instanceCreator, LaunchType launchType) {
        B b = instanceCreator.getOrCreate(B.class, launchType);

        return new A(b, instanceCreator.getOrCreate(Stepper.class));
    }
}
