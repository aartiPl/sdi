package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class CCreator extends CreatorBase<C, LaunchType> {
    @Override
    public C create(InstanceProvider instanceProvider, LaunchType launchType) {
        A a = instanceProvider.getOrCreate(A.class, launchType);
        B b = instanceProvider.getOrCreate(B.class, launchType);

        return new C(a, b, instanceProvider.getOrCreate(Stepper.class));
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new ACreator(), new BCreator());
    }
}
