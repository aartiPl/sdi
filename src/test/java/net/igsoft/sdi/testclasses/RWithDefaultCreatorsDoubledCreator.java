package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class RWithDefaultCreatorsDoubledCreator extends CreatorBase<RClass, LaunchType> {
    @Override
    public RClass create(InstanceProvider instanceProvider, LaunchType launchType) {
        DClass d = instanceProvider.getOrCreate(DClass.class);

        return new RClass(instanceProvider.getOrCreate(Stepper.class), "a", "b");
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new DDoubledCreator(), new AutoCreator(Stepper.class));
    }
}
