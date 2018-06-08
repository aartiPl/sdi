package net.igsoft.sdi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.igsoft.sdi.internal.Instance;
import net.igsoft.sdi.internal.KeyGenerator;
import net.igsoft.sdi.internal.ManageableState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service implements Manageable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private final Map<String, ManageableState> states;
    private final KeyGenerator keyGenerator;
    private final Map<String, Instance> instances;
    private final List<Collection<String>> sortedLevels;

    Service(KeyGenerator keyGenerator, Map<String, Instance> instances,
            List<Collection<String>> sortedLevels) {
        this.keyGenerator = keyGenerator;
        this.instances = instances;
        this.sortedLevels = sortedLevels;
        this.states = Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    public <T, P extends ParametersBase> T get(Class<T> clazz, P params) {
        return (T) instances.get(keyGenerator.generate(clazz, params.cachedUniqueId()))
                            .getValue();
    }

    @SuppressWarnings("unchecked")
    public <T, P extends ParametersBase> T get(Class<T> clazz) {
        return (T) instances.get(keyGenerator.generate(clazz, ""))
                .getValue();
    }

    @Override
    public void init() {
        applyOperation(ManageableBasic.class, Lists.reverse(sortedLevels),
                       Lists.newArrayList(ManageableState.CREATED), ManageableState.INITIALIZED,
                       ManageableBasic::init);
    }

    @Override
    public void start() {
        applyOperation(Manageable.class, Lists.reverse(sortedLevels),
                       Lists.newArrayList(ManageableState.INITIALIZED, ManageableState.STOPPED),
                       ManageableState.STARTED, Manageable::start);
    }

    @Override
    public void stop() {
        applyOperation(Manageable.class, sortedLevels, Lists.newArrayList(ManageableState.STARTED),
                       ManageableState.STOPPED, Manageable::stop);
    }

    @Override
    public void close() {
        stop();

        applyOperation(ManageableBasic.class, sortedLevels,
                       Lists.newArrayList(ManageableState.STOPPED, ManageableState.INITIALIZED),
                       ManageableState.CLOSED, ManageableBasic::close);
    }

    private <T extends ManageableBasic> void applyOperation(Class<T> clazz,
                                                            List<Collection<String>> levels,
                                                            List<ManageableState> baseState,
                                                            ManageableState finalState,
                                                            Consumer<T> operation) {
        for (Collection<String> ids : levels) {
            for (String id : ids) {
                Instance instance = instances.get(id);
                Object instanceValue = instance.getValue();

                boolean isSubclassOfClazz = clazz.isAssignableFrom(instanceValue.getClass());

                if (isSubclassOfClazz &&
                    baseState.contains(states.getOrDefault(id, ManageableState.CREATED))) {
                    boolean isStartStopOperation = (finalState == ManageableState.STARTED ||
                                                    finalState == ManageableState.STOPPED);

                    if (!(isStartStopOperation && instance.isManualStartAndStop())) {
                        @SuppressWarnings("unchecked")
                        T manageable = (T) instanceValue;
                        operation.accept(manageable);
                        states.put(id, finalState);
                    }
                }
            }
        }
    }

    public static ServiceBuilder builder() {
        return new ServiceBuilder();
    }
}
