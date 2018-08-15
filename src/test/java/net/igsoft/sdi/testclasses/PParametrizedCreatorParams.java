package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.parameter.ParameterBase;

public final class PParametrizedCreatorParams extends ParameterBase {

    private final String id;

    public PParametrizedCreatorParams(boolean manualStartAndStop, String id) {
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
