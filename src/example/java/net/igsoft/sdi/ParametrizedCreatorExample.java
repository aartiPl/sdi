package net.igsoft.sdi;

import java.io.File;

public class ParametrizedCreatorExample {

    // tag::config[]
    static class ConfigCreatorParam extends ParameterBase {
        private final File file;

        public ConfigCreatorParam(File file) {
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

    static class Config {
        static Config createFromFile(File file) {
            return new Config();
        }
    }

    static class ConfigCreator extends Creator<Config, ConfigCreatorParam> {
        @Override
        public Config create(InstanceCreator instanceCreator, ConfigCreatorParam params) {
            File file = params.getFile();
            return Config.createFromFile(file);
        }
    }

    // end::config[]

    static class MqListener {
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

    static class AppCreator extends Creator<App, AppEnvironment> {
        @Override
        public App create(InstanceCreator instanceCreator, AppEnvironment appEnvironment) {
            ConfigCreatorParam params = new ConfigCreatorParam(new File("~/config.init"));
            Config config = instanceCreator.getOrCreate(Config.class, params);
            MqListener mqListener = instanceCreator.getOrCreate(MqListener.class);

            if (appEnvironment.getName().equals("PROD")) {
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
                                 .withCreator(new ConfigCreator(), new ConfigCreatorParam(new File(".")))
                                 .withCreator(new AutoCreator<>(MqListener.class))
                                 .build();

        Runtime.getRuntime().addShutdownHook(new Thread(service::close));

        service.start();
    }
    // end::main[]
}
