package net.igsoft.sdi.testclasses;

public class P {
    public P(Stepper stepper, String id, R r) {
        stepper.addStep(this.getClass(), "ctor(" + id + " r)");
    }
}
