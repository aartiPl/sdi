package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.ParameterBase;

public final class ParametrizedCreator2Params extends ParameterBase {

    private final String id;

    public ParametrizedCreator2Params(boolean manualStartAndStop, String id) {
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
