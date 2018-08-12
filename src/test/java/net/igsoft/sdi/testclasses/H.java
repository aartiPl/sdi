package net.igsoft.sdi.testclasses;

public class H {
    public H(Stepper stepper, G g) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
