package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class BCreator extends Creator<B, LaunchType> {
    @Override
    public B create(InstanceCreator instanceCreator, LaunchType launchType) {
        D d = instanceCreator.getOrCreate(D.class, launchType);
        P p = instanceCreator.getOrCreate(P.class, new PParametrizedCreatorParams(false, "id"));

        return new B(d, p, instanceCreator.getOrCreate(Stepper.class));
    }

    @Override
    public List<Creator<?, ?>> defaultCreators() {
        return Lists.newArrayList(new PParametrizedCreator());
    }
}
