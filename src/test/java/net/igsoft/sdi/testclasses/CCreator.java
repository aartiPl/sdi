package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class CCreator extends Creator<C, LaunchType> {
    @Override
    public C create(InstanceCreator instanceCreator, LaunchType launchType) {
        A a = instanceCreator.getOrCreate(A.class, launchType);
        B b = instanceCreator.getOrCreate(B.class, launchType);

        return new C(a, b, instanceCreator.getOrCreate(Stepper.class));
    }

    @Override
    public List<Creator<?, ?>> defaultCreators() {
        return Lists.newArrayList(new ACreator(), new BCreator());
    }
}
