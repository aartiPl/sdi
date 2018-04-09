package net.igsoft.sdi.classes;

import net.igsoft.sdi.Stepper;

public class E {
    public E(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
