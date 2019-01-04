package net.igsoft.sdi.testclasses;

public class PClass {
    public PClass(Stepper stepper, String id, RClass r) {
        stepper.addStep(this.getClass(), "ctor(" + id + " r)");
    }
}
