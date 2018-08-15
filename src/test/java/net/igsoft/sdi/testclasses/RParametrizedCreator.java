package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.InstanceProvider;

public class RParametrizedCreator extends CreatorBase<R, RParametrizedCreatorParams> {
    @Override
    public R create(InstanceProvider instanceProvider,
                    RParametrizedCreatorParams RParametrizedCreatorParams) {
        return new R(instanceProvider.getOrCreate(Stepper.class),
                     RParametrizedCreatorParams.getName(), RParametrizedCreatorParams.getSurname());
    }
}
