package net.igsoft.sdi.example;

import java.io.File;

import net.igsoft.sdi.Service;
import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.engine.InstanceProvider;
import net.igsoft.sdi.parameter.ParameterBase;

public class ParametrizedCreatorExample {

    // tag::config[]
    public static class Config {
        static Config createFromFile(File file) {
            return new Config();
        }
    }

    public static class ConfigCreator extends CreatorBase<Config, ConfigCreator.Params> {
        @Override
        public Config create(InstanceProvider instanceProvider, ConfigCreator.Params params) {
            File file = params.getFile();
            return Config.createFromFile(file);
        }

        public static class Params extends ParameterBase {
            private final File file;

            public Params(File file) {
                super(false);
                this.file = file;
            }

            public File getFile() {
                return file;
            }

            @Override
            public String uniqueId() {
                return file.getName();
            }
        }
    }

    // end::config[]

    public static class MqListener {
    }

    // tag::app[]
    static class AppEnvironment extends ParameterBase {
        private final String name;

        public AppEnvironment(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String uniqueId() {
            return name;
        }
    }

    static class App {
        public App(Config e, MqListener mqListener) {
        }
    }

    static class AppCreator extends CreatorBase<App, AppEnvironment> {
        @Override
        public App create(InstanceProvider instanceProvider, AppEnvironment appEnvironment) {
            ConfigCreator.Params params = new ConfigCreator.Params(new File("~/config.init"));
            Config config = instanceProvider.getOrCreate(Config.class, params);
            MqListener mqListener = instanceProvider.getOrCreate(MqListener.class);

            if ("PROD".equals(appEnvironment.getName())) {
                System.out.println("Warning! Creating PROD version of application!");
            }

            return new App(config, mqListener);
        }
    }
    // end::app[]

    // tag::main[]
    public static void main(String[] args) {
        Service service = Service.builder()
                                 .withRootCreator(new AppCreator(), new AppEnvironment("PROD"))
                                 .withCreator(new ConfigCreator(), new ConfigCreator.Params(new File(".")))
                                 .withCreator(new AutoCreator<>(MqListener.class))
                                 .build();

        Runtime.getRuntime().addShutdownHook(new Thread(service::close));

        service.start();
    }
    // end::main[]
}
