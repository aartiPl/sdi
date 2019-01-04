package net.igsoft.sdi.testclasses;

public class RClass {
    public RClass(Stepper stepper, String name, String surname) {
        stepper.addStep(this.getClass(), "ctor(" + name + " " + surname + ")");
    }
}
