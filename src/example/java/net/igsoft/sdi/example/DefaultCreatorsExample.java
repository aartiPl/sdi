package net.igsoft.sdi.example;

import java.util.List;

import com.google.common.collect.Lists;

import net.igsoft.sdi.Service;
import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class DefaultCreatorsExample {

    public static class Config {
    }

    // tag::default_creators[]
    public static class MqListenerWorker {
    }

    public static class MqListenerCreator extends CreatorBase<MqListener, LaunchType> {
        @Override
        public MqListener create(InstanceProvider instanceProvider, LaunchType params) {
            MqListenerWorker mqListenerWorker = instanceProvider.getOrCreate(MqListenerWorker.class);
            return new MqListener(mqListenerWorker);
        }

        @Override
        public List<CreatorBase<?, ?>> defaultCreators() {
            return Lists.newArrayList(new AutoCreator<>(MqListenerWorker.class));
        }
    }

    public static class MqListener {
        public MqListener(MqListenerWorker mqListenerWorker) {
        }
    }

    // end::default_creators[]

    public static class App {
        public App(Config e, MqListener mqListener) {
        }
    }

    // tag::main[]

    public static void main(String[] args) {
        Service service = Service.builder()
                                 .withRootCreator(new AutoCreator<>(App.class))
                                 .withCreator(new MqListenerCreator())
                                 .withCreator(new AutoCreator<>(Config.class))
                                 .build();

        Runtime.getRuntime().addShutdownHook(new Thread(service::close));

        service.start();
    }

    // end::main[]
}
