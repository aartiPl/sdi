package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;

public class PParametrizedCreator extends Creator<P, PParametrizedCreatorParams> {
    @Override
    public P create(InstanceCreator instanceCreator,
                    PParametrizedCreatorParams PParametrizedCreatorParams) {
        R r = instanceCreator.getOrCreate(R.class,
                                          new RParametrizedCreatorParams("name", "surname"));

        return new P(instanceCreator.getOrCreate(Stepper.class), PParametrizedCreatorParams.getId(),
                     r);
    }
}
