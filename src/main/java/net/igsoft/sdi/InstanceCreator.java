package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import com.google.common.collect.Maps;
import net.igsoft.sdi.internal.Instance;
import net.igsoft.sdi.internal.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCreator.class);

    private final Map<Class<?>, Creator<?>> creators;
    private final Map<Class<?>, Creator<?>> defaultCreators;
    private final Map<Class<?>, Creator<?>> unusedCreators;
    private final KeyGenerator keyGenerator;
    private final Deque<Instance> stack;
    private final Map<String, Instance> instances;

    InstanceCreator(Map<Class<?>, Creator<?>> creators,
                           Map<Class<?>, Creator<?>> defaultCreators, KeyGenerator keyGenerator) {
        this.creators = creators;
        this.unusedCreators = Maps.newHashMap(creators);
        this.defaultCreators = defaultCreators;
        this.keyGenerator = keyGenerator;
        this.instances = Maps.newHashMap();
        this.stack = new ArrayDeque<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(Class<?> clazz, CreatorParams params, boolean manualStartAndStop) {
        //NOTE: ManualStartAndStop does not differentiate instances!
        String instanceKey = keyGenerator.generate(clazz, params.getSerializedParameters());

        if (!stack.isEmpty()) {
            stack.peek().addDependency(instanceKey);
        }

        Instance instance = instances.computeIfAbsent(instanceKey, s -> new Instance());
        stack.push(instance);

        if (instance.getLevel() == 0) {
            //It's just freshly created instance...
            instance.setValue(calculateInstanceValue(clazz, params));
            instance.setLevel(stack.size());
        }

        instance.manualStartAndStop(manualStartAndStop);

        if (instance.getLevel() < stack.size()) {
            pushDown(instance, stack.size() - instance.getLevel());
        }

        stack.poll();

        return (T) instance.getValue();
    }

    public <T> T getOrCreate(Class<?> clazz, boolean manualStartAndStop) {
        return getOrCreate(clazz, CreatorParams.EMPTY_PARAMS, manualStartAndStop);
    }

    public <T> T getOrCreate(Class<?> clazz, CreatorParams params) {
        return getOrCreate(clazz, params, false);
    }

    public <T> T getOrCreate(Class<?> clazz) {
        return getOrCreate(clazz, CreatorParams.EMPTY_PARAMS, false);
    }

    public Map<Class<?>, Creator<?>> getUnusedCreators() {
        return unusedCreators;
    }

    public Map<String, Instance> getInstances() {
        return instances;
    }

    @SuppressWarnings("unchecked")
    private <T> T calculateInstanceValue(Class<T> clazz, CreatorParams params) {
        T instanceValue;

        Creator<T> creator = (Creator<T>) creators.get(clazz);
        unusedCreators.remove(clazz);

        if (creator == null) {
            LOGGER.info(
                    "No explicit creator has been found for class: {}. Looking in default creators...",
                    clazz.getName());

            creator = (Creator<T>) defaultCreators.get(clazz);
            LOGGER.info("Default creator for class {} has {}been found", clazz.getName(),
                        creator == null ? "not " : "");
        }

        checkState(creator != null, "No creator has been found for class: " + clazz.getName());

        if (!params.isEmpty()) {
            instanceValue = creator.create(this, params);
            if (!params.areAllUsed()) {
                LOGGER.warn(
                        "Not all parameters were used during construction of '{}'. Unused parameters: {}",
                        clazz.getName(), params.unusedParameters());
            }
        } else {
            instanceValue = creator.create(this);
        }
        return instanceValue;
    }

    private void pushDown(Instance instance, int levelDistance) {
        int newLevel = instance.getLevel() + levelDistance;
        instance.setLevel(newLevel);

        for (String dependency : instance.getDependencies()) {
            pushDown(instances.get(dependency), levelDistance);
        }
    }
}
