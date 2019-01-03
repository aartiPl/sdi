package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class E1Creator extends CreatorBase<EClass, LaunchType> {
    @Override
    public EClass create(InstanceProvider instanceProvider, LaunchType launchType) {
        return new EClass(instanceProvider.getOrCreate(Stepper.class));
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new DDoubledCreator());
    }
}
