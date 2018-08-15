package net.igsoft.sdi;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.LaunchType;

public class SimpleCreatorExample {

    // tag::config[]
    static class Config {
    }

    static class ConfigCreator extends CreatorBase<Config, LaunchType> {
        @Override
        public Config create(InstanceProvider instanceProvider, LaunchType launchType) {
            return new Config();
        }
    }
    // end::config[]

    // tag::app[]
    static class App {
        public App(Config e) {
        }
    }

    static class AppCreator extends CreatorBase<App, LaunchType> {
        @Override
        public App create(InstanceProvider instanceProvider, LaunchType launchType) {
            Config config = instanceProvider.getOrCreate(Config.class);
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
