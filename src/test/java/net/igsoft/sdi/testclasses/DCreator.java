package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class DCreator extends CreatorBase<DClass, LaunchType> {
    @Override
    public DClass create(InstanceProvider instanceProvider, LaunchType launchType) {
        EClass e = instanceProvider.getOrCreate(EClass.class, launchType);
        return new DClass(e, instanceProvider.getOrCreate(Stepper.class));
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new ECreator());
    }
}
