package net.igsoft.sdi.classes;

import net.igsoft.sdi.CreatorBase;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.Stepper;

public class ECreator extends CreatorBase<E> {

    private final Stepper stepper;

    public ECreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public E create(InstanceCreator instanceCreator) {
        return new E(stepper);
    }
}
