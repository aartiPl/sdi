package net.igsoft.sdi

import java.util

import com.google.common.collect.Lists

object DefaultCreatorsScalaExample {

  private[sdi] class Config

  // tag::default_creators[]
  private[sdi] class MqListenerWorker

  private[sdi] class MqListenerCreator extends Creator[MqListener, LaunchType] {
    override def create(instanceCreator: InstanceCreator, params: LaunchType): MqListener = {
      val mqListenerWorker = instanceCreator.getOrCreate(classOf[MqListenerWorker])
      new MqListener(mqListenerWorker)
    }

    override def defaultCreators: util.List[Creator[_, _ <: ParameterBase]] = Lists
                                                                              .newArrayList(new AutoCreator[MqListenerWorker, ParameterBase](classOf[MqListenerWorker]))
  }

  private[sdi] class MqListener(val mqListenerWorker: MqListenerWorker) {
  }

  // end::default_creators[]
  private[sdi] class App(val e: Config, val mqListener: MqListener) {
  }

  // tag::main[]
  def main(args: Array[String]): Unit = {
    val service = Service.builder.withRootCreator(new AutoCreator[App, ParameterBase](classOf[App]))
                  .withCreator(new MqListenerCreator)
                  .withCreator(new AutoCreator[Config, ParameterBase](classOf[Config])).build
    sys.ShutdownHookThread {
      service.close()
    }

    service.start()
  }

  // end::main[]
}
