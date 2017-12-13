package net.igsoft.sdi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.igsoft.sdi.internal.ManageableState;
import net.igsoft.sdi.internal.MapKeyGenerator;
import net.igsoft.sdi.internal.ServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service implements Manageable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private final Class<?> mainClass;
    private final MapKeyGenerator keyGenerator;
    private final Map<String, Object> instances;
    private final List<Collection<String>> sortedLevels;
    private final Map<String, Boolean> manualStartAndStopMap;
    private final Map<String, ManageableState> states;

    Service(Class<?> mainClass, MapKeyGenerator keyGenerator, Map<String, Object> instances,
            List<Collection<String>> sortedLevels, Map<String, Boolean> manualStartAndStopMap) {
        this.mainClass = mainClass;
        this.keyGenerator = keyGenerator;
        this.instances = instances;
        this.sortedLevels = sortedLevels;
        this.manualStartAndStopMap = manualStartAndStopMap;
        this.states = Maps.newHashMap();
    }

    public <T> T get(Class<T> clazz) {
        return get(clazz, CreatorParams.EMPTY_PARAMS);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, CreatorParams params) {
        return (T) instances.get(keyGenerator.generate(clazz, params.getSerializedParameters()));
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
                Object instance = instances.get(id);

                boolean isSubclassOfClazz = clazz.isAssignableFrom(instance.getClass());

                if (isSubclassOfClazz &&
                    baseState.contains(states.getOrDefault(id, ManageableState.CREATED))) {
                    boolean isStartStopOperation = (finalState == ManageableState.STARTED ||
                                                    finalState == ManageableState.STOPPED);

                    if (!isStartStopOperation || !manualStartAndStopMap.getOrDefault(id, false)) {
                        @SuppressWarnings("unchecked")
                        T manageable = (T) instance;
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
