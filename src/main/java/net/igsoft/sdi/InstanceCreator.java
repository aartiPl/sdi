package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.igsoft.sdi.internal.Instance;
import net.igsoft.sdi.internal.KeyGenerator;
import net.igsoft.sdi.internal.Specification;

public class InstanceCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCreator.class);

    private final Map<Class<?>, Specification> specification;
    private final Map<String, Specification> runtimeSpecification;
    private final Map<Class<?>, Specification> unusedCreators;
    private final KeyGenerator keyGenerator;
    private final Deque<Specification> stack;

    InstanceCreator(Map<Class<?>, Specification> specification, KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
        this.specification = specification;
        this.runtimeSpecification = new HashMap<>();
        this.unusedCreators = new HashMap<>(specification);
        this.stack = new ArrayDeque<>();
    }

    public <R> R getOrCreate(Class<?> clazz) {
        return getOrCreate(clazz, specification.get(clazz).getDefaultParameter());
    }

    public <P extends ParameterBase, R> R getOrCreate(Class<?> clazz, P params) {
        //NOTE: ManualStartAndStop does not differentiate runtimeSpecification!

        String instanceKey = keyGenerator.generate(clazz, params.cachedUniqueId());

        if (!stack.isEmpty()) {
            stack.peek().addDependency(instanceKey);
        }

        Specification specification =
                runtimeSpecification.computeIfAbsent(instanceKey, s -> new Specification());
        stack.push(specification);

        if (specification.getLevel() == 0) {
            //It's just freshly created specification...
            specification.setValue(calculateInstanceValue(clazz, params));
            specification.setLevel(stack.size());
        }

        specification.manualStartAndStop(params.isManualStartAndStop());

        if (specification.getLevel() < stack.size()) {
            pushDown(specification, stack.size() - specification.getLevel());
        }

        stack.poll();

        return (R) specification.getValue();
    }

    public Map<Class<?>, Specification> getUnusedCreators() {
        return unusedCreators;
    }

    public Map<String, Specification> getRuntimeSpecification() {
        return runtimeSpecification;
    }

    public Map<String, Instance> getInstances() {
        return runtimeSpecification.entrySet()
                                   .stream()
                                   .collect(Collectors.toMap(e -> e.getKey(), e -> new Instance(
                                           e.getValue().getValue(),
                                           e.getValue().isManualStartAndStop())));
    }

    @SuppressWarnings("unchecked")
    private <P extends ParameterBase, R> R calculateInstanceValue(Class<?> clazz, P params) {
        Specification specification = this.specification.get(clazz);
        Creator<R, P> creator = (Creator<R, P>) specification.getCreator();

        unusedCreators.remove(clazz);

        if (creator == null) {
            LOGGER.info(
                    "No explicit creator has been found for class: {}. Looking in default creators...",
                    clazz.getName());

            creator = (Creator<R, P>) specification.getDefaultCreator();

            LOGGER.info("Default creator for class {} has {}been found", clazz.getName(),
                        creator == null ? "not " : "");
        }

        checkState(creator != null, "No creator has been found for class: " + clazz.getName());

        return creator.create(this, params);
    }

    private void pushDown(Specification specification, int levelDistance) {
        int newLevel = specification.getLevel() + levelDistance;
        specification.setLevel(newLevel);

        for (String dependency : specification.getDependencies()) {
            pushDown(runtimeSpecification.get(dependency), levelDistance);
        }
    }
}
