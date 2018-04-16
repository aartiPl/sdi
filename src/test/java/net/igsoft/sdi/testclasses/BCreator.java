package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;

public class BCreator extends Creator<B> {

    private final Stepper stepper;

    public BCreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public B create(InstanceCreator instanceCreator) {
        D d = instanceCreator.getOrCreate(D.class);

        return new B(d, stepper);
    }
}
