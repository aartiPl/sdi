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

    private final Map<String, Instance> instances;
    private final Deque<String> stack;

    public InstanceCreator(Map<Class<?>, Creator<?>> creators,
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
            instances.get(stack.peek()).addDependency(instanceKey);
        }

        stack.push(instanceKey);


        //NOTE: seems like a bug in JDK - below implementation (computeIfAbsent) doesn't work while standard implementation (checking for null) works
        //        T instance = (T) instances.computeIfAbsent(instanceKey, s -> {
        //          instance = createInstance(clazz, params);
        //          LOGGER.debug("Inserting to instances: {} -> {}", instanceKey, instance);\
        //          return instance;
        //        });

//        T instance = (T) instances.get(instanceKey);

        Instance instance = instances.get(instanceKey);

        if (instance == null) {
            instance = new Instance();                    ;
            LOGGER.debug("Inserting to instances: {} -> {}", instanceKey, instance);
            instances.put(instanceKey, instance);
            instance.setValue(createInstanceValue(clazz, params));
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

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstanceValue(Class<T> clazz, CreatorParams params) {
        T instance;

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
            instance = creator.create(this, params);
            if (!params.areAllUsed()) {
                LOGGER.warn(
                        "Not all parameters were used during construction of '{}'. Unused parameters: {}",
                        clazz.getName(), params.unusedParameters());
            }
        } else {
            instance = creator.create(this);
        }
        return instance;
    }

    private void pushDown(Instance instance, int levelDistance) {
        int newLevel = instance.getLevel() + levelDistance;
        instance.setLevel(newLevel);

        for (String dependency : instance.getDependencies()) {
            pushDown(instances.get(dependency), levelDistance);
        }
    }
}
