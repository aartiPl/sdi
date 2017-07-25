package net.igsoft.sdi;

public class E {
    public E(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
