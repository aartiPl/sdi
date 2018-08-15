package net.igsoft.sdi

import net.igsoft.sdi.creator.CreatorBase
import net.igsoft.sdi.internal.InstanceProvider
import net.igsoft.sdi.parameter.LaunchType

object SimpleCreatorScalaExample { // tag::config[]

  private[sdi] class Config

  private[sdi] class ConfigCreator extends CreatorBase[Config, LaunchType] {
    override def create(instanceProvider: InstanceProvider, launchType: LaunchType) = new Config
  }

  // tag::app[]
  // end::config[]
  private[sdi] class App(val e: Config)

  private[sdi] class AppCreator extends CreatorBase[App, LaunchType] {
    override def create(instanceProvider: InstanceProvider, launchType: LaunchType): App = {
      val config = instanceProvider.getOrCreate(classOf[Config])
      new App(config)
    }
  }

  // tag::main[]
  // end::app[]
  def main(args: Array[String]): Unit = {
    val service = Service.builder.withRootCreator(new AppCreator).withCreator(new ConfigCreator)
                  .build

    sys.ShutdownHookThread {
      service.close()
    }

    service.start()
  }

  // end::main[]
}
