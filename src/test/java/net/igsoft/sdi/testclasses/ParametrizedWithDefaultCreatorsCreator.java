package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.AutoCreator;
import net.igsoft.sdi.Creator;
import net.igsoft.sdi.InstanceCreator;

public class ParametrizedWithDefaultCreatorsCreator
        extends Creator<R, ParametrizedWithDefaultCreatorsCreatorParams> {
    @Override
    public R create(InstanceCreator instanceCreator,
                    ParametrizedWithDefaultCreatorsCreatorParams parametrizedCreatorParams) {
        //Test that C, D and E are available
        C c = instanceCreator.getOrCreate(C.class);
        D d = instanceCreator.getOrCreate(D.class);

        return new R(instanceCreator.getOrCreate(Stepper.class),
                     parametrizedCreatorParams.getName(), parametrizedCreatorParams.getSurname());
    }

    @Override
    public List<Creator<?, ?>> defaultCreators() {
        return Lists.newArrayList(new CCreator(), new DCreator(), new AutoCreator(Stepper.class));
    }
}
