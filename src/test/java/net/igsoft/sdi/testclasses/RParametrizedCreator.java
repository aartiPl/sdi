package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;

public class RParametrizedCreator extends Creator<R, RParametrizedCreatorParams> {
    @Override
    public R create(InstanceCreator instanceCreator,
                    RParametrizedCreatorParams RParametrizedCreatorParams) {
        return new R(instanceCreator.getOrCreate(Stepper.class),
                     RParametrizedCreatorParams.getName(), RParametrizedCreatorParams.getSurname());
    }
}
