package net.igsoft.sdi

object AutoCreatorScalaExample {

  private[sdi] class Config {}

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

  private[sdi] class App(val e: Config, val mqListner: MqListener) {
  }

  def main(args: Array[String]): Unit = {
    val service = Service.builder.withRootCreator(new AutoCreator[App, ParameterBase](classOf[App]))
                  .withCreator(new AutoCreator[Config, ParameterBase](classOf[Config]))
                  .withCreator(new AutoCreator[MqListener, ParameterBase](classOf[MqListener]))
                  .build

    sys.ShutdownHookThread {
      service.close()
    }

    service.start()
  }
}
