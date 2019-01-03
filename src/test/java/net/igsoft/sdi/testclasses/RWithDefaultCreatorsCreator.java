package net.igsoft.sdi.testclasses;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.ParameterBase;

public class RWithDefaultCreatorsCreator
        extends CreatorBase<R, RWithDefaultCreatorsCreator.Params> {

    @Override
    public R create(InstanceProvider instanceProvider, Params params) {
        //Test that C, D and E are available
        D d = instanceProvider.getOrCreate(D.class);

        return new R(instanceProvider.getOrCreate(Stepper.class), params.getName(),
                     params.getSurname());
    }

    @Override
    public List<CreatorBase<?, ?>> defaultCreators() {
        return Lists.newArrayList(new DCreator(), new AutoCreator(Stepper.class));
    }

    public static final class Params extends ParameterBase {

        private final String name;
        private final String surname;

        public Params(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        @Override
        public String uniqueId() {
            return concatenate(name, surname);
        }
    }
}
