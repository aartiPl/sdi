package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class CCreator extends CreatorBase<CClass, LaunchType> {
    @Override
    public CClass create(InstanceProvider instanceProvider, LaunchType launchType) {
        AClass a = instanceProvider.getOrCreate(AClass.class, launchType);
        BClass b = instanceProvider.getOrCreate(BClass.class, launchType);

        return new CClass(a, b, instanceProvider.getOrCreate(Stepper.class));
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new ACreator(), new BCreator());
    }
}
