package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkArgument;
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

    private final Map<Class<?>, Creator<?, ?>> creators;
    private final Map<Class<?>, Creator<?, ?>> defaultCreators;
    private final Map<Class<?>, ParameterBase> defaultParameters;
    private final KeyGenerator keyGenerator;

    private final Map<Class<?>, Creator<?, ?>> unusedCreators;
    private final Map<String, Specification> runtimeSpecification;
    private final Deque<Specification> stack;

    public InstanceCreator(Map<Class<?>, Creator<?, ?>> creators,
                           Map<Class<?>, Creator<?, ?>> defaultCreators,
                           Map<Class<?>, ParameterBase> defaultParameters,
                           KeyGenerator keyGenerator) {
        this.creators = creators;
        this.defaultCreators = defaultCreators;
        this.defaultParameters = defaultParameters;
        this.keyGenerator = keyGenerator;

        this.unusedCreators = new HashMap<>(creators);
        this.runtimeSpecification = new HashMap<>();
        this.stack = new ArrayDeque<>();
    }

    public <R> R getOrCreate(Class<?> clazz) {
        return getOrCreate(clazz, defaultParameters.get(clazz));
    }

    public <P extends ParameterBase, R> R getOrCreate(Class<?> clazz, P params) {
        checkArgument(clazz != null);
        checkArgument(params != null, new IllegalArgumentException(
                format("There is no parameter provided for creator neither explicitly nor through default parameters of creator of the class %s",
                       clazz)));

        String instanceKey = keyGenerator.generate(clazz, params.cachedUniqueId());

        if (!stack.isEmpty()) {
            stack.peek().addDependency(instanceKey);
        }

        Specification specification =
                this.runtimeSpecification.computeIfAbsent(instanceKey, s -> new Specification());
        stack.push(specification);

        if (specification.getLevel() == 0) {
            //It's just freshly created runtimeSpecification...
            specification.setValue(calculateInstanceValue(clazz, params));
            specification.setLevel(stack.size());
        }

        specification.manualStartAndStop(params.isManualStartAndStop());

        if (specification.getLevel() < stack.size()) {
            pushDown(specification, stack.size() - specification.getLevel());
        }

        stack.pop();

        return (R) specification.getValue();
    }

    public Map<Class<?>, Creator<?, ?>> getUnusedCreators() {
        return unusedCreators;
    }

    public Map<String, Specification> getRuntimeSpecification() {
        return runtimeSpecification;
    }

    public Map<String, Instance> getInstances() {
        return runtimeSpecification.entrySet()
                                   .stream()
                                   .collect(Collectors.toMap(Map.Entry::getKey, e -> new Instance(
                                           e.getValue().getValue(),
                                           e.getValue().isManualStartAndStop())));
    }

    @SuppressWarnings("unchecked")
    private <P extends ParameterBase, R> R calculateInstanceValue(Class<?> clazz, P params) {
        Creator<R, P> creator = (Creator<R, P>) creators.get(clazz);

        unusedCreators.remove(clazz);

        if (creator == null) {
            LOGGER.info(
                    "No explicit creator has been found for class: {}. Looking in default creators...",
                    clazz.getName());

            creator = (Creator<R, P>) defaultCreators.get(clazz);

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
            pushDown(this.runtimeSpecification.get(dependency), levelDistance);
        }
    }
}
