package net.igsoft.sdi

import java.io.File

import net.igsoft.sdi.creator.{AutoCreator, CreatorBase}
import net.igsoft.sdi.internal.InstanceProvider
import net.igsoft.sdi.parameter.ParameterBase

object ParametrizedCreatorScalaExample {

  // tag::config[]
  private[sdi] case class ConfigCreatorParam(val file: File) extends ParameterBase(false) {
    override def uniqueId: String = file.getName
  }

  private[sdi] object Config {
    private[sdi] def createFromFile(file: File) = new Config
  }

  private[sdi] class Config {}

  private[sdi] class ConfigCreator extends CreatorBase[Config, ConfigCreatorParam] {
    override def create(instanceProvider: InstanceProvider, params: ConfigCreatorParam): Config = {
      val file = params.file
      Config.createFromFile(file)
    }
  }

  // end::config[]
  private[sdi] class MqListener {}

  // tag::app[]
  private[sdi] case class AppEnvironment(val name: String) extends ParameterBase {
    override def uniqueId: String = name
  }

  private[sdi] class App(val e: Config, val mqListener: MqListener)

  private[sdi] class AppCreator extends CreatorBase[App, AppEnvironment] {
    override def create(instanceProvider: InstanceProvider, appEnvironment: AppEnvironment): App = {
      val params = new ConfigCreatorParam(new File("~/config.init"))
      val config = instanceProvider.getOrCreate(classOf[Config], params)
      val mqListener = instanceProvider.getOrCreate(classOf[MqListener])

      if (appEnvironment.name == "PROD") println("Warning! Creating PROD version of application!")

      new App(config, mqListener)
    }
  }

  // tag::main[]
  // end::app[]
  def main(args: Array[String]): Unit = {
    val service = Service.builder.withRootCreator(new AppCreator, new AppEnvironment("PROD"))
                  .withCreator(new ConfigCreator, new ConfigCreatorParam(new File(".")))
                  .withCreator(new AutoCreator[MqListener, ParameterBase](classOf[MqListener]))
                  .build
    sys.ShutdownHookThread {
      service.close()
    }
    service.start()
  }

  // end::main[]
}
