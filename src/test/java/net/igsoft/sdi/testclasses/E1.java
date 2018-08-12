package net.igsoft.sdi.testclasses;

public class E1 {
    public E1(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
