package net.igsoft.sdi.internal;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Instance {

    private Object value;
    private int level;
    private boolean manualStartAndStop;
    private List<String> dependencies;

    public Instance() {
        this.value = null;
        this.level = 0;
        this.manualStartAndStop = false;
        this.dependencies = Lists.newArrayList();
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

    public List<String> getDependencies() {
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
        Instance instance = (Instance) o;
        return level == instance.level &&
               manualStartAndStop == instance.manualStartAndStop &&
               Objects.equals(value, instance.value) &&
               Objects.equals(dependencies, instance.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, level, manualStartAndStop, dependencies);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("value", value)
                                        .append("level", level)
                                        .append("manualStartAndStop", manualStartAndStop)
                                        .append("dependencies", dependencies)
                                        .toString();
    }
}
