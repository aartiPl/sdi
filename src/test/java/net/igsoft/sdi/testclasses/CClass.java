package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Manageable;

public class CClass implements Manageable {

    private final AClass a;
    private final BClass b;
    private final Stepper stepper;

    public CClass(AClass a, BClass b, Stepper stepper) {
        this.a = a;
        this.b = b;
        this.stepper = stepper;
        stepper.addStep(this.getClass(), "ctor");
    }

    @Override
    public void init() {
        stepper.addStep(this.getClass(), "init");
    }

    @Override
    public void start() {
        stepper.addStep(this.getClass(), "start");
    }

    @Override
    public void stop() {
        stepper.addStep(this.getClass(), "stop");
    }

    @Override
    public void close() {
        stepper.addStep(this.getClass(), "close");
    }
}
