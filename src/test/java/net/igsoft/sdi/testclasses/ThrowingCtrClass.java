package net.igsoft.sdi.testclasses;

public class ThrowingCtrClass {
    public ThrowingCtrClass(Stepper stepper) {
        throw new IllegalStateException();
    }
}
