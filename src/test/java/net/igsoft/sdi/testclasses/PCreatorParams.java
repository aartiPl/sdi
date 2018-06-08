package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.ParametersBase;

public final class PCreatorParams extends ParametersBase {

    private final String id;

    public PCreatorParams(String id, boolean manualStartAndStop) {
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
