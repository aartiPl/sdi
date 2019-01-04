package net.igsoft.sdi.testclasses;

public class PrivateCtrClass {
    private PrivateCtrClass(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
