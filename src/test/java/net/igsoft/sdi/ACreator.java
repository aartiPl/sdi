package net.igsoft.sdi;

class ACreator extends Creator<A> {

    private final Stepper stepper;

    public ACreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public A create(InstanceCreator instanceCreator) {
        B b = instanceCreator.getOrCreate(B.class);

        return new A(b, stepper);
    }
}
