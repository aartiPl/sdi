package net.igsoft.sdi.example;

import net.igsoft.sdi.Manageable;
import net.igsoft.sdi.Service;
import net.igsoft.sdi.creator.AutoCreator;

public class AutoCreatorExample {

    public static class Config {
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

    public static class App {
        public App(Config e, MqListener mqListener) {
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
