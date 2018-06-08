package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class ECreator extends Creator<E, LaunchType> {
    @Override
    public E create(InstanceCreator instanceCreator, LaunchType launchType) {
        return new E(instanceCreator.getOrCreate(Stepper.class));
    }
}
