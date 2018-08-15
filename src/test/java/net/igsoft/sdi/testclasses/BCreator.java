package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class BCreator extends CreatorBase<B, LaunchType> {
    @Override
    public B create(InstanceProvider instanceProvider, LaunchType launchType) {
        D d = instanceProvider.getOrCreate(D.class, launchType);
        P p = instanceProvider.getOrCreate(P.class, new PParametrizedCreatorParams(false, "id"));

        return new B(d, p, instanceProvider.getOrCreate(Stepper.class));
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new PParametrizedCreator());
    }
}
