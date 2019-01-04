package net.igsoft.sdi.testclasses;

public class E1Class {
    public E1Class(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
