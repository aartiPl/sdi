package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;

public class RCreator extends Creator<R, RCreatorParams> {
    @Override
    public R create(InstanceCreator instanceCreator, RCreatorParams rCreatorParams) {
        return new R(instanceCreator.getOrCreate(Stepper.class), rCreatorParams.getName(),
                     rCreatorParams.getSurname());
    }
}
