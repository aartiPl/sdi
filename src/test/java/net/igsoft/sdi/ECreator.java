package net.igsoft.sdi;

class ECreator extends Creator<E> {

    private final Stepper stepper;

    public ECreator(Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public E create(InstanceCreator instanceCreator) {
        return new E(stepper);
    }
}
