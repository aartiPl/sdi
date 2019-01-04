package net.igsoft.sdi.testclasses;

public class QClass {
    public QClass(Stepper stepper, String id, RClass r) {
        stepper.addStep(this.getClass(), "ctor(" + id + " r)");
    }

    public QClass(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor()");
    }
}
