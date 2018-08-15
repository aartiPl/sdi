package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.InstanceProvider;

public class PParametrizedCreator extends CreatorBase<P, PParametrizedCreatorParams> {
    @Override
    public P create(InstanceProvider instanceProvider,
                    PParametrizedCreatorParams PParametrizedCreatorParams) {
        R r = instanceProvider.getOrCreate(R.class,
                                          new RParametrizedCreatorParams("name", "surname"));

        return new P(instanceProvider.getOrCreate(Stepper.class), PParametrizedCreatorParams.getId(),
                     r);
    }
}
