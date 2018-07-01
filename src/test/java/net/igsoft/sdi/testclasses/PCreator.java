package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;

public class PCreator extends Creator<P, PCreatorParams> {
    @Override
    public P create(InstanceCreator instanceCreator, PCreatorParams pCreatorParams) {
        R r = instanceCreator.getOrCreate(R.class, new RCreatorParams("name", "surname"));

        return new P(instanceCreator.getOrCreate(Stepper.class), pCreatorParams.getId(), r);
    }
}
