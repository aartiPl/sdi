package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Manageable;

public class DClass implements Manageable {

    private final Stepper stepper;

    public DClass(EClass e, Stepper stepper) {
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
