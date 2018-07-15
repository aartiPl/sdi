package net.igsoft.sdi;

public abstract class ParametersBase {
    private final boolean manualStartAndStop;
    private String serialized;

    protected ParametersBase(boolean manualStartAndStop) {
        this.manualStartAndStop = manualStartAndStop;
    }

    protected ParametersBase() {
        this(false);
    }

    public boolean isManualStartAndStop() {
        return manualStartAndStop;
    }

    public abstract String uniqueId();

    //Single threaded
    public String cachedUniqueId() {
        if (serialized == null) {
            serialized = uniqueId();
        }

        return serialized;
    }
}
