package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;

public class RWithDefaultCreatorsCreator extends CreatorBase<R, RWithDefaultCreatorsCreatorParams> {
    @Override
    public R create(InstanceProvider instanceProvider,
                    RWithDefaultCreatorsCreatorParams parametrizedCreatorParams) {
        //Test that C, D and E are available
        D d = instanceProvider.getOrCreate(D.class);

        return new R(instanceProvider.getOrCreate(Stepper.class),
                     parametrizedCreatorParams.getName(), parametrizedCreatorParams.getSurname());
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new DCreator(), new AutoCreator(Stepper.class));
    }
}
