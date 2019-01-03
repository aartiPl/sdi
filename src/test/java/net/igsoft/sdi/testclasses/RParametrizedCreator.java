package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.ParameterBase;

public class RParametrizedCreator extends CreatorBase<RClass, RParametrizedCreator.Params> {

    @Override
    public RClass create(InstanceProvider instanceProvider, Params params) {
        return new RClass(instanceProvider.getOrCreate(Stepper.class), params.getName(),
                          params.getSurname());
    }

    public static final class Params extends ParameterBase {

        private final String name;
        private final String surname;

        Params(String name, String surname) {
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
