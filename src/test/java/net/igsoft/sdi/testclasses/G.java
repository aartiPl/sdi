package net.igsoft.sdi.testclasses;

public class G {
    public G(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
