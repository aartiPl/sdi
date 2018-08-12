package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;
import net.igsoft.sdi.LaunchType;

public class E1Creator extends Creator<E, LaunchType> {
    @Override
    public E create(InstanceCreator instanceCreator, LaunchType launchType) {
        return new E(instanceCreator.getOrCreate(Stepper.class));
    }

    @Override
    public List<Creator<?, ?>> defaultCreators() {
        return Lists.newArrayList(new DDoubledCreator());
    }
}
