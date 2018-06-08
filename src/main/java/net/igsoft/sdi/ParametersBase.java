package net.igsoft.sdi;

public abstract class ParametersBase {
    private String serialized = null;
    private boolean manualStartAndStop;

    protected ParametersBase(boolean manualStartAndStop) {
        this.manualStartAndStop = manualStartAndStop;
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
