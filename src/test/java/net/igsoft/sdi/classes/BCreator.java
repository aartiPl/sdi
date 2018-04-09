package net.igsoft.sdi.classes;

import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.Stepper;

public class BCreator extends CreatorBase<B> {

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
