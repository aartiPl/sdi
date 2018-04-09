package net.igsoft.sdi.classes;

import net.igsoft.sdi.Stepper;

public class H {
    public H(Stepper stepper, G g) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
