package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.ParameterBase;

public class PParametrizedCreator extends CreatorBase<PClass, PParametrizedCreator.Params> {

    @Override
    public PClass create(InstanceProvider instanceProvider, Params params) {
        RClass r = instanceProvider.getOrCreate(RClass.class,
                                                new RParametrizedCreator.Params("name", "surname"));

        return new PClass(instanceProvider.getOrCreate(Stepper.class), params.getId(), r);
    }

    public static final class Params extends ParameterBase {

        private final String id;

        public Params(boolean manualStartAndStop, String id) {
            super(manualStartAndStop);
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public String uniqueId() {
            return id;
        }
    }
}
