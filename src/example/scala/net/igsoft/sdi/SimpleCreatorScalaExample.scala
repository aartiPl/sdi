package net.igsoft.sdi

object SimpleCreatorScalaExample { // tag::config[]

  private[sdi] class Config

  private[sdi] class ConfigCreator extends Creator[Config, LaunchType] {
    override def create(instanceCreator: InstanceCreator, launchType: LaunchType) = new Config
  }

  // tag::app[]
  // end::config[]
  private[sdi] class App(val e: Config)

  private[sdi] class AppCreator extends Creator[App, LaunchType] {
    override def create(instanceCreator: InstanceCreator, launchType: LaunchType): App = {
      val config = instanceCreator.getOrCreate(classOf[Config])
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
