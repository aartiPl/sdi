package net.igsoft.sdi.classes;

import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.Stepper;

public class CCreator extends CreatorBase<C> {

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
