package net.igsoft.sdi.parameter;

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

    protected final String concatenate(String... parts) {
        return String.join("_", parts);
    }

    //Single threaded
    public String cachedUniqueId() {
        if (serialized == null) {
            serialized = uniqueId();
        }

        return serialized;
    }
}
