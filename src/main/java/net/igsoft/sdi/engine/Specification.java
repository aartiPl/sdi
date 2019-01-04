package net.igsoft.sdi.engine;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Specification {

    private final Set<String> dependencies;
    private Object value;
    private int level;
    private boolean manualStartAndStop;

    public Specification() {
        this.value = null;
        this.level = 0;
        this.manualStartAndStop = false;
        this.dependencies = new HashSet<>();
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
