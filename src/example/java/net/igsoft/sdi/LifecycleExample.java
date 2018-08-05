package net.igsoft.sdi;

public class LifecycleExample {

    // tag::classes[]
    static class Config {
    }

    static class ConfigCreator extends Creator<Config, LaunchType> {
        @Override
        public Config create(InstanceCreator instanceCreator, LaunchType launchType) {
            return new Config();
        }
    }

    static class MqListener implements Manageable {
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

    static class MqListenerCreator extends Creator<MqListener, LaunchType> {
        @Override
        public MqListener create(InstanceCreator instanceCreator, LaunchType launchType) {
            return new MqListener();
        }
    }

    static class App {
        public App(Config e, MqListener mqListner) {
        }
    }

    static class AppCreator extends Creator<App, LaunchType> {
        @Override
        public App create(InstanceCreator instanceCreator, LaunchType launchType) {
            Config config = instanceCreator.getOrCreate(Config.class);
            MqListener mqListener = instanceCreator.getOrCreate(MqListener.class);
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
