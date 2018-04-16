package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;

public class ECreator extends Creator<E> {

    private final Stepper stepper;

    public ECreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public E create(InstanceCreator instanceCreator) {
        return new E(stepper);
    }
}
