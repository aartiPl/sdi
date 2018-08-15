package net.igsoft.sdi.engine;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public class Specification {

    private final Set<String> dependencies;
    private Object value;
    private int level;
    private boolean manualStartAndStop;

    public Specification() {
        this.value = null;
        this.level = 0;
        this.manualStartAndStop = false;
        this.dependencies = Sets.newHashSet();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isManualStartAndStop() {
        return manualStartAndStop;
    }

    public void manualStartAndStop(boolean manualStartAndStop) {
        //NOTE: if at least once it is requested to start/stop manually
        //then other settings to false doesn't matter
        this.manualStartAndStop = this.manualStartAndStop || manualStartAndStop;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void addDependency(String instanceId) {
        dependencies.add(instanceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Specification specification = (Specification) o;
        return level == specification.level &&
               manualStartAndStop == specification.manualStartAndStop &&
               Objects.equals(value, specification.value) &&
               Objects.equals(dependencies, specification.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, level, manualStartAndStop, dependencies);
    }

    @Override
    public String toString() {
        return "Specification{" +
               "value=" +
               value +
               ", level=" +
               level +
               ", manualStartAndStop=" +
               manualStartAndStop +
               ", dependencies=" +
               dependencies +
               '}';
    }
}
