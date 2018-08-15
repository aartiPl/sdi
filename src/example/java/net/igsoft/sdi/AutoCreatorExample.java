package net.igsoft.sdi;

import net.igsoft.sdi.creator.AutoCreator;

public class AutoCreatorExample {

    static class Config {
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

    static class App {
        public App(Config e, MqListener mqListner) {
        }
    }

    public static void main(String[] args) {
        Service service = Service.builder()
                                       .withRootCreator(new AutoCreator<>(App.class))
                                       .withCreator(new AutoCreator<>(Config.class))
                                       .withCreator(new AutoCreator<>(MqListener.class))
                                       .build();

        Runtime.getRuntime().addShutdownHook(new Thread(service::close));

        service.start();
    }
}
