package net.igsoft.sdi.example

import net.igsoft.sdi.Service
import net.igsoft.sdi.creator.CreatorBase
import net.igsoft.sdi.engine.InstanceProvider
import net.igsoft.sdi.parameter.LaunchType

object SimpleCreatorScalaExample {

  // tag::config[]
  private[sdi] class Config

  private[sdi] class ConfigCreator extends CreatorBase[Config, LaunchType] {
    override def create(instanceProvider: InstanceProvider, launchType: LaunchType) = new Config
  }
  // end::config[]

  // tag::app[]
  private[sdi] class App(val e: Config)

  private[sdi] class AppCreator extends CreatorBase[App, LaunchType] {
    override def create(instanceProvider: InstanceProvider, launchType: LaunchType): App = {
      val config = instanceProvider.getOrCreate(classOf[Config])
      new App(config)
    }
  }
  // end::app[]

  // tag::main[]
  def main(args: Array[String]): Unit = {
    val service = Service.builder
                  .withRootCreator(new AppCreator)
                  .withCreator(new ConfigCreator)
                  .build

    sys.ShutdownHookThread {
      service.close()
    }

    service.start()
  }
  // end::main[]
}
