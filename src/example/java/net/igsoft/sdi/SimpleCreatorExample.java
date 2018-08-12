package net.igsoft.sdi;

public class SimpleCreatorExample {

    // tag::config[]
    static class Config {
    }

    static class ConfigCreator extends Creator<Config, LaunchType> {
        @Override
        public Config create(InstanceCreator instanceCreator, LaunchType launchType) {
            return new Config();
        }
    }
    // end::config[]

    // tag::app[]
    static class App {
        public App(Config e) {
        }
    }

    static class AppCreator extends Creator<App, LaunchType> {
        @Override
        public App create(InstanceCreator instanceCreator, LaunchType launchType) {
            Config config = instanceCreator.getOrCreate(Config.class);
            return new App(config);
        }
    }
    // end::app[]

    // tag::main[]
    public static void main(String[] args) {
        Service service = Service.builder()
                                 .withRootCreator(new AppCreator())
                                 .withCreator(new ConfigCreator())
                                 .build();

        Runtime.getRuntime().addShutdownHook(new Thread(service::close));

        service.start();
    }
    // end::main[]
}
