package net.igsoft.sdi

import net.igsoft.sdi.creator.AutoCreator
import net.igsoft.sdi.parameter.ParameterBase

object AutoCreatorScalaExample {

  private[sdi] class Config

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

  private[sdi] class App(val e: Config, val mqListener: MqListener)

  def main(args: Array[String]): Unit = {
    val service = Service.builder
                  .withRootCreator(new AutoCreator(classOf[App]))
                  .withCreator(new AutoCreator(classOf[Config]))
                  .withCreator(new AutoCreator(classOf[MqListener]))
                  .build

    sys.ShutdownHookThread {
      service.close()
    }

    service.start()
  }
}
