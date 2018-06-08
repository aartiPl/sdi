package net.igsoft.sdi.testclasses;

public class R {
    public R(Stepper stepper, String name, String surname) {
        stepper.addStep(this.getClass(), "ctor(" + name + " " + surname + ")");
    }
}
