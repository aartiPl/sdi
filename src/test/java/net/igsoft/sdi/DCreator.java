package net.igsoft.sdi;

class DCreator extends Creator<D> {

    private final Stepper stepper;

    public DCreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public D create(InstanceCreator instanceCreator) {
        E e = instanceCreator.getOrCreate(E.class);
        return new D(e, stepper);
    }
}
