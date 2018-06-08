package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class DCreator extends Creator<D, LaunchType> {
    @Override
    public D create(InstanceCreator instanceCreator, LaunchType launchType) {
        E e = instanceCreator.getOrCreate(E.class, launchType);
        return new D(e, instanceCreator.getOrCreate(Stepper.class));
    }
}
