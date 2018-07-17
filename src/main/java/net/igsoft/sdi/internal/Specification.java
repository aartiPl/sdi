package net.igsoft.sdi.internal;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

import net.igsoft.sdi.Creator;
import net.igsoft.sdi.ParameterBase;

public class Specification {

    private final Set<String> dependencies;
    private Creator<?, ?> creator;
    private Creator<?, ?> defaultCreator;
    private boolean rootCreator;
    private ParameterBase defaultParameter;
    private Object value;
    private int level;
    private boolean manualStartAndStop;

    public Specification() {
        this.defaultCreator = null;
        this.defaultParameter = null;
        this.rootCreator = false;
        this.creator = null;
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
        return new StringBuilder("Specification{").append("value=")
                                                  .append(value)
                                                  .append(", level=")
                                                  .append(level)
                                                  .append(", manualStartAndStop=")
                                                  .append(manualStartAndStop)
                                                  .append(", dependencies=")
                                                  .append(dependencies)
                                                  .append('}')
                                                  .toString();
    }

    public Creator<?, ?> getCreator() {
        return creator;
    }

    public void setCreator(Creator<?, ?> creator) {
        this.creator = creator;
    }

    public boolean isRootCreator() {
        return rootCreator;
    }

    public void setRootCreator(boolean rootCreator) {
        this.rootCreator = rootCreator;
    }

    public ParameterBase getDefaultParameter() {
        return defaultParameter;
    }

    public void setDefaultParameter(ParameterBase defaultParameter) {
        this.defaultParameter = defaultParameter;
    }

    public Creator<?, ?> getDefaultCreator() {
        return defaultCreator;
    }

    public void setDefaultCreator(Creator<?, ?> defaultCreator) {
        this.defaultCreator = defaultCreator;
    }
}
