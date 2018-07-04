package net.igsoft.sdi.testclasses;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

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

    @Override
    public String toString() {
        return steps.stream().collect(Collectors.joining(" "));
    }
}
