package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Manageable;

class AClass implements Manageable {
    private final Stepper stepper;

    public AClass(BClass b, Stepper stepper) {
        this.stepper = stepper;
        stepper.addStep(this.getClass(), "ctor");
    }

    public String hello() {
        stepper.addStep(this.getClass(), "hello");
        return "Hello World";
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
