package net.igsoft.sdi.internal;

import java.util.Objects;

public class Instance {
    private final Object value;
    private final boolean manualStartAndStop;

    public Instance(Object value, boolean manualStartAndStop) {
        this.value = value;
        this.manualStartAndStop = manualStartAndStop;
    }

    public Object getValue() {
        return value;
    }

    public boolean isManualStartAndStop() {
        return manualStartAndStop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Instance instance = (Instance) o;
        return manualStartAndStop == instance.manualStartAndStop &&
               Objects.equals(value, instance.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, manualStartAndStop);
    }

    @Override
    public String toString() {
        return "Instance{" + "value=" + value + ", manualStartAndStop=" + manualStartAndStop + '}';
    }
}
