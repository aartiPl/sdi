package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.AutoCreator;
import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class RWithDefaultCreatorsDoubledCreator extends Creator<R, LaunchType> {
    @Override
    public R create(InstanceCreator instanceCreator, LaunchType launchType) {
        D d = instanceCreator.getOrCreate(D.class);

        return new R(instanceCreator.getOrCreate(Stepper.class), "a", "b");
    }

    @Override
    public List<Creator<?, ?>> defaultCreators() {
        return Lists.newArrayList(new DDoubledCreator(), new AutoCreator(Stepper.class));
    }
}
