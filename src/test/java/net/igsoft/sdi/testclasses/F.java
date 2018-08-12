package net.igsoft.sdi.testclasses;

public class F {
    public F(Stepper stepper, G g, H h) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
