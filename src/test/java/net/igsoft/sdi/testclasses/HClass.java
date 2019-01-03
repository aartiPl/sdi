package net.igsoft.sdi.testclasses;

public class HClass {
    public HClass(Stepper stepper, GClass g) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
