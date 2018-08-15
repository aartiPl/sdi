package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class DCreator extends CreatorBase<D, LaunchType> {
    @Override
    public D create(InstanceProvider instanceProvider, LaunchType launchType) {
        E e = instanceProvider.getOrCreate(E.class, launchType);
        return new D(e, instanceProvider.getOrCreate(Stepper.class));
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new ECreator());
    }
}
