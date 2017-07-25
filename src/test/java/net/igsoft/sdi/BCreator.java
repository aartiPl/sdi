package net.igsoft.sdi;

class BCreator extends Creator<B> {

    private final Stepper stepper;

    public BCreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public B create(InstanceCreator instanceCreator) {
        D d = instanceCreator.getOrCreate(D.class);

        return new B(d, stepper);
    }
}
