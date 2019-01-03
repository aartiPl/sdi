package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class BCreator extends CreatorBase<BClass, LaunchType> {
    @Override
    public BClass create(InstanceProvider instanceProvider, LaunchType launchType) {
        DClass d = instanceProvider.getOrCreate(DClass.class, launchType);
        PClass p = instanceProvider.getOrCreate(PClass.class, new PParametrizedCreator.Params(false, "id"));

        return new BClass(d, p, instanceProvider.getOrCreate(Stepper.class));
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new PParametrizedCreator());
    }
}
