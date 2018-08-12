package net.igsoft.sdi;

import java.util.List;

import com.google.common.collect.Lists;

public class DefaultCreatorsExample {

    static class Config {
    }

    // tag::default_creators[]
    static class MqListenerWorker {
    }

    static class MqListenerCreator extends Creator<MqListener, LaunchType> {
        @Override
        public MqListener create(InstanceCreator instanceCreator, LaunchType params) {
            MqListenerWorker mqListenerWorker = instanceCreator.getOrCreate(MqListenerWorker.class);
            return new MqListener(mqListenerWorker);
        }

        @Override
        public List<Creator<?, ?>> defaultCreators() {
            return Lists.newArrayList(new AutoCreator<>(MqListenerWorker.class));
        }
    }

    static class MqListener {
        public MqListener(MqListenerWorker mqListenerWorker) {
        }
    }

    // end::default_creators[]

    static class App {
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
