package net.igsoft.sdi

import net.igsoft.sdi.creator.CreatorBase
import net.igsoft.sdi.internal.InstanceProvider
import net.igsoft.sdi.parameter.LaunchType

object LifecycleScalaExample {

  // tag::classes[]
  private[sdi] class Config

  private[sdi] class ConfigCreator extends CreatorBase[Config, LaunchType] {
    override def create(instanceProvider: InstanceProvider, launchType: LaunchType) = new Config
  }

  private[sdi] class MqListener extends Manageable {
    override def init(): Unit = {
      //Initialize class
    }

    override def start(): Unit = {
      //Start class
    }

    override def stop(): Unit = {
      //Stop class (with ability to start it again)
    }

    override def close(): Unit = {
      //Destruct class
    }
  }

  private[sdi] class MqListenerCreator extends CreatorBase[MqListener, LaunchType] {
    override def create(instanceProvider: InstanceProvider, launchType: LaunchType) = new MqListener
  }

  private[sdi] class App(val e: Config, val mqListner: MqListener) {
  }

  private[sdi] class AppCreator extends CreatorBase[App, LaunchType] {
    override def create(instanceProvider: InstanceProvider, launchType: LaunchType): App = {
      val config = instanceProvider.getOrCreate(classOf[Config])
      val mqListener = instanceProvider.getOrCreate(classOf[MqListener])
      new App(config, mqListener)
    }
  }

  // tag::main[]
  // end::classes[]
  def main(args: Array[String]): Unit = {
    val service = Service.builder.withRootCreator(new AppCreator).withCreator(new ConfigCreator)
                  .withCreator(new MqListenerCreator).build

    sys.ShutdownHookThread {
      service.close()
    }
    service.start()
  }

  // end::main[]
}
