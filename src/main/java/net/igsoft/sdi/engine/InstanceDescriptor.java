package net.igsoft.sdi.engine;

import java.util.Objects;

public class InstanceDescriptor {
    private final Object value;
    private final boolean manualStartAndStop;

    public InstanceDescriptor(Object value, boolean manualStartAndStop) {
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
        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }
        InstanceDescriptor instance = (InstanceDescriptor) o;
        return manualStartAndStop == instance.manualStartAndStop &&
               Objects.equals(value, instance.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, manualStartAndStop);
    }

    @Override
    public String toString() {
        return "InstanceDescriptor{" + "value=" + value + ", manualStartAndStop=" + manualStartAndStop + '}';
    }
}
