package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;

public class ParametrizedCreator1 extends Creator<R, ParametrizedCreator1Params> {
    @Override
    public R create(InstanceCreator instanceCreator, ParametrizedCreator1Params parametrizedCreator1Params) {
        return new R(instanceCreator.getOrCreate(Stepper.class), parametrizedCreator1Params.getName(),
                     parametrizedCreator1Params.getSurname());
    }
}
