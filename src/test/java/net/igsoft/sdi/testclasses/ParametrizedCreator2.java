package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;

public class ParametrizedCreator2 extends Creator<P, ParametrizedCreator2Params> {
    @Override
    public P create(InstanceCreator instanceCreator, ParametrizedCreator2Params parametrizedCreator2Params) {
        R r = instanceCreator.getOrCreate(R.class, new ParametrizedCreator1Params("name", "surname"));

        return new P(instanceCreator.getOrCreate(Stepper.class), parametrizedCreator2Params.getId(), r);
    }
}
