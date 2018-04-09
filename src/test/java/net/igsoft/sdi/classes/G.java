package net.igsoft.sdi.classes;

import net.igsoft.sdi.Stepper;

public class G {
    public G(Stepper stepper) {
        stepper.addStep(this.getClass(), "ctor");
    }
}
