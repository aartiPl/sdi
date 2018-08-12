package net.igsoft.sdi.testclasses;

public class E {
    public E(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
