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

    private final Map<Class<?>, Creator<?, ?>> creators;
    private final Map<Class<?>, Creator<?, ?>> defaultCreators;
    private final Map<Class<?>, Creator<?, ?>> unusedCreators;
    private final KeyGenerator keyGenerator;
    private final Deque<Instance> stack;
    private final Map<String, Instance> instances;

    InstanceCreator(Map<Class<?>, Creator<?, ?>> creators,
                    Map<Class<?>, Creator<?, ?>> defaultCreators,
                    KeyGenerator keyGenerator) {
        this.creators = creators;
        this.unusedCreators = Maps.newHashMap(creators);
        this.defaultCreators = defaultCreators;
        this.keyGenerator = keyGenerator;
        this.instances = Maps.newHashMap();
        this.stack = new ArrayDeque<>();
    }

    public <R> R getOrCreate(Class<?> clazz) {
        return getOrCreate(clazz, LaunchType.AUTOMATIC);
    }

    public <P extends ParametersBase, R> R getOrCreate(Class<?> clazz, P params) {
        //NOTE: ManualStartAndStop does not differentiate instances!

        String instanceKey = keyGenerator.generate(clazz, params.cachedUniqueId());

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

        instance.manualStartAndStop(params.isManualStartAndStop());

        if (instance.getLevel() < stack.size()) {
            pushDown(instance, stack.size() - instance.getLevel());
        }

        stack.poll();

        return (R) instance.getValue();
    }

    public Map<Class<?>, Creator<?, ?>> getUnusedCreators() {
        return unusedCreators;
    }

    public Map<String, Instance> getInstances() {
        return instances;
    }

    @SuppressWarnings("unchecked")
    private <P extends ParametersBase, R> R calculateInstanceValue(Class<?> clazz, P params) {
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

    private void pushDown(Instance instance, int levelDistance) {
        int newLevel = instance.getLevel() + levelDistance;
        instance.setLevel(newLevel);

        for (String dependency : instance.getDependencies()) {
            pushDown(instances.get(dependency), levelDistance);
        }
    }
}
