package net.igsoft.sdi.classes;

import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.Stepper;

public class DCreator extends CreatorBase<D> {

    private final Stepper stepper;

    public DCreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public D create(InstanceCreator instanceCreator) {
        E e = instanceCreator.getOrCreate(E.class);
        return new D(e, stepper);
    }
}
