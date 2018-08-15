package net.igsoft.sdi;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class LifecycleExample {

    // tag::classes[]
    public static class Config {
    }

    public static class ConfigCreator extends CreatorBase<Config, LaunchType> {
        @Override
        public Config create(InstanceProvider instanceProvider, LaunchType launchType) {
            return new Config();
        }
    }

    public static class MqListener implements Manageable {
        @Override
        public void init() {
            //Initialize class
        }

        @Override
        public void start() {
            //Start class
        }

        @Override
        public void stop() {
            //Stop class (with ability to start it again)
        }

        @Override
        public void close() {
            //Destruct class
        }
    }

    public static class MqListenerCreator extends CreatorBase<MqListener, LaunchType> {
        @Override
        public MqListener create(InstanceProvider instanceProvider, LaunchType launchType) {
            return new MqListener();
        }
    }

    public static class App {
        public App(Config e, MqListener mqListner) {
        }
    }

    public static class AppCreator extends CreatorBase<App, LaunchType> {
        @Override
        public App create(InstanceProvider instanceProvider, LaunchType launchType) {
            Config config = instanceProvider.getOrCreate(Config.class);
            MqListener mqListener = instanceProvider.getOrCreate(MqListener.class);
            return new App(config, mqListener);
        }
    }
    // end::classes[]

    // tag::main[]
    public static void main(String[] args) {
        Service service = Service.builder()
                                 .withRootCreator(new AppCreator())
                                 .withCreator(new ConfigCreator())
                                 .withCreator(new MqListenerCreator())
                                 .build();

        Runtime.getRuntime().addShutdownHook(new Thread(service::close));

        service.start();
    }
    // end::main[]
}
