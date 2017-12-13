package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.igsoft.sdi.internal.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCreator.class);

    private final Map<Class<?>, Creator<?>> creators;
    private final Map<Class<?>, Creator<?>> defaultCreators;
    private final Map<Class<?>, Creator<?>> unusedCreators;

    private final KeyGenerator keyGenerator;

    private final Map<String, Object> instances;
    private final Map<String, Boolean> manualStartAndStopMap;
    private final Map<String, Integer> levels;
    private final Multimap<String, String> dependencies;

    private final Deque<String> stack;

    public InstanceCreator(Map<Class<?>, Creator<?>> creators,
                           Map<Class<?>, Creator<?>> defaultCreators, KeyGenerator keyGenerator) {
        this.creators = creators;
        this.unusedCreators = Maps.newHashMap(creators);
        this.defaultCreators = defaultCreators;
        this.keyGenerator = keyGenerator;
        this.instances = Maps.newHashMap();
        this.manualStartAndStopMap = Maps.newHashMap();
        this.levels = Maps.newHashMap();
        this.dependencies = ArrayListMultimap.create();
        this.stack = new ArrayDeque<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(Class<?> clazz, CreatorParams params, boolean manualStartAndStop) {
        //NOTE: ManualStartAndStop does not differentiate instances!
        String instanceKey = keyGenerator.generate(clazz, params.getSerializedParameters());

        if (!stack.isEmpty()) {
            dependencies.put(stack.peek(), instanceKey);
        }

        stack.push(instanceKey);

        if (manualStartAndStop) {
            manualStartAndStopMap.put(instanceKey, true);
        }

        //LOGGER.debug("Before getting instance of {}", clazz);
        //NOTE: seems like a bug in JDK - below implementation (computeIfAbsent) doesn't work while standard implementation (checking for null) works
        //        T instance = (T) instances.computeIfAbsent(instanceKey, s -> {
        //          return createInstance(clazz, params);
        //        });

        T instance = (T) instances.get(instanceKey);

        if (instance == null) {
            instance = createInstance(clazz, params);

            LOGGER.debug("Inserting to instances: {} -> {}", instanceKey, instance);
            instances.put(instanceKey, instance);
        }

        //LOGGER.debug("After getting instance of {} - instance: {}", clazz, instance);

        Integer oldLevel = levels.get(instanceKey);
        if (oldLevel == null) {
            levels.put(instanceKey, stack.size());
        } else if (oldLevel < stack.size()) {
            pushDown(instanceKey, stack.size() - oldLevel);
        }

        stack.poll();

        return instance;
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

    public Multimap<String, String> getDependencies() {
        return dependencies;
    }

    public Map<Class<?>, Creator<?>> getUnusedCreators() {
        return unusedCreators;
    }

    public Map<String, Integer> getLevels() {
        return levels;
    }

    public Map<String, Object> getInstances() {
        return instances;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public Map<String, Boolean> getManualStartAndStopMap() {
        return manualStartAndStopMap;
    }

    private <T> T createInstance(Class<?> clazz, CreatorParams params) {
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

    private void pushDown(String instanceKey, int levelDistance) {
        int newLevel = levels.get(instanceKey) + levelDistance;
        levels.put(instanceKey, newLevel);

        for (String dependency : dependencies.get(instanceKey)) {
            pushDown(dependency, levelDistance);
        }
    }
}
