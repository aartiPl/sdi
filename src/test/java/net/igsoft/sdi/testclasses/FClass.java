package net.igsoft.sdi.testclasses;

public class FClass {
    public FClass(Stepper stepper, GClass g, HClass h) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
