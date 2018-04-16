package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;

public class ACreator extends Creator<A> {

    private final Stepper stepper;

    public ACreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public A create(InstanceCreator instanceCreator) {
        B b = instanceCreator.getOrCreate(B.class);

        return new A(b, stepper);
    }
}
