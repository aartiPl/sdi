package net.igsoft.sdi.testclasses;

public class GClass {
    public GClass(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
