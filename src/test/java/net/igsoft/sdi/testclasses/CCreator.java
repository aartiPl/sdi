package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;

public class CCreator extends Creator<C> {

    private final Stepper stepper;

    public CCreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public C create(InstanceCreator instanceCreator) {
        A a = instanceCreator.getOrCreate(A.class);
        B b = instanceCreator.getOrCreate(B.class);

        return new C(a, b, stepper);
    }
}
