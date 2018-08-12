package net.igsoft.sdi.testclasses;

public class Q {
    public Q(Stepper stepper, String id, R r) {
        stepper.addStep(this.getClass(), "ctor(" + id + " r)");
    }

    public Q(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor()");
    }
}
