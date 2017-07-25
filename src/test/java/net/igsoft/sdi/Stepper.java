package net.igsoft.sdi;

import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

public class Stepper {
    private final List<String> steps;

    public Stepper() {
        this.steps = Lists.newArrayList();
    }

    public void addStep(Class<?> clazz, String step) {
        steps.add(clazz.getSimpleName() + ":" + step);
    }

    public List<String> getSteps() {
        return steps;
    }

    public String toString() {
        return StringUtils.join(steps, ' ');
    }
}
