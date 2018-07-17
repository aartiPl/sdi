package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.igsoft.sdi.internal.Instance;
import net.igsoft.sdi.internal.KeyGenerator;
import net.igsoft.sdi.internal.Specification;

public class InstanceCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCreator.class);

    private final Map<Class<?>, Specification> specificationMap;
    private final Map<String, Specification> runtimeSpecificationMap;
    private final Map<Class<?>, Specification> unusedCreators;
    private final KeyGenerator keyGenerator;
    private final Deque<Specification> stack;

    InstanceCreator(Map<Class<?>, Specification> specificationMap, KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
        this.specificationMap = specificationMap;
        this.runtimeSpecificationMap = new HashMap<>();
        this.unusedCreators = new HashMap<>(specificationMap);
        this.stack = new ArrayDeque<>();
    }

    public <R> R getOrCreate(Class<?> clazz) {
        return getOrCreate(clazz, specificationMap.get(clazz).getDefaultParameter());
    }

    public <P extends ParameterBase, R> R getOrCreate(Class<?> clazz, P params) {
        //NOTE: ManualStartAndStop does not differentiate runtimeSpecificationMap!

        String instanceKey = keyGenerator.generate(clazz, params.cachedUniqueId());

        if (!stack.isEmpty()) {
            stack.peek().addDependency(instanceKey);
        }

        Specification specification =
                runtimeSpecificationMap.computeIfAbsent(instanceKey, s -> specificationMap.get(clazz));
        stack.push(specification);

        if (specification.getLevel() == 0) {
            //It's just freshly created specificationMap...
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

    public Map<String, Specification> getRuntimeSpecificationMap() {
        return runtimeSpecificationMap;
    }

    public Map<String, Instance> getInstances() {
        return runtimeSpecificationMap.entrySet()
                                      .stream()
                                      .collect(Collectors.toMap(e -> e.getKey(), e -> new Instance(
                                              e.getValue().getValue(),
                                              e.getValue().isManualStartAndStop())));
    }

    @SuppressWarnings("unchecked")
    private <P extends ParameterBase, R> R calculateInstanceValue(Class<?> clazz, P params) {
        Specification specification = specificationMap.computeIfAbsent(clazz, s -> new Specification());
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
        checkState(creator.getParameterClass().isAssignableFrom(params.getClass()),
                   format("Invalid parameter class '%s' for creator '%s' ('%s' expected)",
                          params.getClass().getSimpleName(), creator.getClass().getSimpleName(),
                          creator.getParameterClass().getSimpleName()));

        return creator.create(this, params);
    }

    private <R, P extends ParameterBase> void pushDown(Specification specification, int levelDistance) {
        int newLevel = specification.getLevel() + levelDistance;
        specification.setLevel(newLevel);

        for (String dependency : specification.getDependencies()) {
            pushDown(runtimeSpecificationMap.get(dependency), levelDistance);
        }
    }
}
