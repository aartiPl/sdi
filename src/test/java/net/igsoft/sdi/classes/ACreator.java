package net.igsoft.sdi.classes;

import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.Stepper;

public class ACreator extends CreatorBase<A> {

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
