package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class RWithDefaultCreatorsDoubledCreator extends CreatorBase<R, LaunchType> {
    @Override
    public R create(InstanceProvider instanceProvider, LaunchType launchType) {
        D d = instanceProvider.getOrCreate(D.class);

        return new R(instanceProvider.getOrCreate(Stepper.class), "a", "b");
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new DDoubledCreator(), new AutoCreator(Stepper.class));
    }
}
