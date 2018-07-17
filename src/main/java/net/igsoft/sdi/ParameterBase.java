package net.igsoft.sdi;

public abstract class ParameterBase {
    private final boolean manualStartAndStop;
    private String serialized;

    protected ParameterBase(boolean manualStartAndStop) {
        this.manualStartAndStop = manualStartAndStop;
    }

    protected ParameterBase() {
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
