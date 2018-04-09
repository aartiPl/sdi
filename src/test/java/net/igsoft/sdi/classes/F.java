package net.igsoft.sdi.classes;

import net.igsoft.sdi.Stepper;

public class F {
    public F(Stepper stepper, G g, H h) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
