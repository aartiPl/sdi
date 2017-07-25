package net.igsoft.sdi;

class CCreator extends Creator<C> {

    private final Stepper stepper;

    public CCreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public C create(InstanceCreator instanceCreator) {
        A a = instanceCreator.getOrCreate(A.class);
        B b = instanceCreator.getOrCreate(B.class);

        return new C(a, b, stepper);
    }
}
