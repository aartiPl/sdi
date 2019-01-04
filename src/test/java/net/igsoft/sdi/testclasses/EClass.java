package net.igsoft.sdi.testclasses;

public class EClass {
    public EClass(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
