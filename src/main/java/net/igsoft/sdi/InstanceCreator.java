package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import net.igsoft.sdi.internal.Instance;
import net.igsoft.sdi.internal.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCreator.class);

    private final Map<Class<?>, CreatorBase<?>> creators;
    private final Map<Class<?>, CreatorBase<?>> defaultCreators;
    private final Map<Class<?>, CreatorBase<?>> unusedCreators;
    private final KeyGenerator keyGenerator;
    private final Deque<Instance> stack;
    private final Map<String, Instance> instances;

    InstanceCreator(Map<Class<?>, CreatorBase<?>> creators,
                    Map<Class<?>, CreatorBase<?>> defaultCreators, KeyGenerator keyGenerator) {
        this.creators = creators;
        this.unusedCreators = Maps.newHashMap(creators);
        this.defaultCreators = defaultCreators;
        this.keyGenerator = keyGenerator;
        this.instances = Maps.newHashMap();
        this.stack = new ArrayDeque<>();
    }



    public <P, R> R getOrCreate(Class<?> clazz, P params, boolean manualStartAndStop) {
        return getOrCreateInternal(clazz, serialize(params), creatorBase -> ((ParametrizedCreator<P, R>)creatorBase).create(InstanceCreator.this, params), manualStartAndStop);
    }

    public <R> R getOrCreate(Class<?> clazz, boolean manualStartAndStop) {
        return getOrCreateInternal(clazz, "", creatorBase -> ((Creator<R>)creatorBase).create(InstanceCreator.this), manualStartAndStop);
    }

    public <P, R> R getOrCreate(Class<?> clazz, P params) {
        return getOrCreateInternal(clazz, serialize(params), creatorBase -> ((ParametrizedCreator<P, R>)creatorBase).create(InstanceCreator.this, params), false);
    }

    public <R> R getOrCreate(Class<?> clazz) {
        return getOrCreateInternal(clazz, "", creatorBase -> ((Creator<R>)creatorBase).create(InstanceCreator.this), false);
    }

    public Map<Class<?>, CreatorBase<?>> getUnusedCreators() {
        return unusedCreators;
    }

    public Map<String, Instance> getInstances() {
        return instances;
    }

    private <P> String serialize(P params) {
        Field[] declaredFields = params.getClass().getDeclaredFields();

        for(Field field : declaredFields) {

        }

        return params.toString();
    }

    @SuppressWarnings("unchecked")
    private <P, R> R getOrCreateInternal(Class<?> clazz, String serializedParams, Function<CreatorBase<R>, R> fn, boolean manualStartAndStop) {
        //NOTE: ManualStartAndStop does not differentiate instances!
        String instanceKey = keyGenerator.generate(clazz, serializedParams);

        if (!stack.isEmpty()) {
            stack.peek().addDependency(instanceKey);
        }

        Instance instance = instances.computeIfAbsent(instanceKey, s -> new Instance());
        stack.push(instance);

        if (instance.getLevel() == 0) {
            //It's just freshly created instance...
            instance.setValue(calculateInstanceValue(clazz, fn));
            instance.setLevel(stack.size());
        }

        instance.manualStartAndStop(manualStartAndStop);

        if (instance.getLevel() < stack.size()) {
            pushDown(instance, stack.size() - instance.getLevel());
        }

        stack.poll();

        return (R) instance.getValue();
    }

    @SuppressWarnings("unchecked")
    private <P, R> R calculateInstanceValue(Class<?> clazz, Function<CreatorBase<R>, R> fn) {
        R instanceValue;

        CreatorBase<R> creatorBase = (CreatorBase<R>) creators.get(clazz);
        unusedCreators.remove(clazz);

        if (creatorBase == null) {
            LOGGER.info(
                    "No explicit creator has been found for class: {}. Looking in default creators...",
                    clazz.getName());

            creatorBase = (CreatorBase<R>) defaultCreators.get(clazz);
            LOGGER.info("Default creator for class {} has {}been found", clazz.getName(),
                        creatorBase == null ? "not " : "");
        }

        checkState(creatorBase != null, "No creator has been found for class: " + clazz.getName());

        return fn.apply(creatorBase);

//        if (!params.isEmpty()) {
//            instanceValue = creatorBase.create(this, params);
//            if (!params.areAllUsed()) {
//                LOGGER.warn(
//                        "Not all parameters were used during construction of '{}'. Unused parameters: {}",
//                        clazz.getName(), params.unusedParameters());
//            }
//        } else {
//            instanceValue = creatorBase.create(this);
//        }
//        return instanceValue;
    }

    private void pushDown(Instance instance, int levelDistance) {
        int newLevel = instance.getLevel() + levelDistance;
        instance.setLevel(newLevel);

        for (String dependency : instance.getDependencies()) {
            pushDown(instances.get(dependency), levelDistance);
        }
    }
}
