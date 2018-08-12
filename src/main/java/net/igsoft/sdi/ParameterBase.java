package net.igsoft.sdi;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        return Arrays.stream(parts).collect(Collectors.joining("_"));
    }

    //Single threaded
    public String cachedUniqueId() {
        if (serialized == null) {
            serialized = uniqueId();
        }

        return serialized;
    }
}
