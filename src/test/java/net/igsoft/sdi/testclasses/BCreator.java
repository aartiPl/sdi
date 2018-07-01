package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class BCreator extends Creator<B, LaunchType> {
    @Override
    public B create(InstanceCreator instanceCreator, LaunchType launchType) {
        D d = instanceCreator.getOrCreate(D.class, launchType);
        P p = instanceCreator.getOrCreate(P.class, new PCreatorParams(false, "id"));

        return new B(d, p, instanceCreator.getOrCreate(Stepper.class));
    }
}
