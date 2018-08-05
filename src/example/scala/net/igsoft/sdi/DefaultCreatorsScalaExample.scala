package net.igsoft.sdi

import java.util

import com.google.common.collect.Lists

object DefaultCreatorsScalaExample {

  class Config

  // tag::default_creators[]
  class MqListenerWorker

  private[sdi] class MqListenerCreator extends Creator[MqListener, LaunchType] {
    override def create(instanceCreator: InstanceCreator, params: LaunchType): MqListener = {
      new MqListener(instanceCreator.getOrCreate(classOf[MqListenerWorker]))
    }

    override def defaultCreators: util.List[Creator[_, _ <: ParameterBase]] = Lists
                                                                              .newArrayList(new AutoCreator[DefaultCreatorsExample.MqListenerWorker, ParameterBase](classOf[DefaultCreatorsExample.MqListenerWorker]))
  }

  class MqListener(val mqListenerWorker: MqListenerWorker)

  // end::default_creators[]
  class App(val e: Config, val mqListener: MqListener)

  // tag::main[]
  def main(args: Array[String]): Unit = {
    val service = Service.builder
                  .withRootCreator(new AutoCreator[DefaultCreatorsExample.App, ParameterBase](classOf[DefaultCreatorsExample.App]))
                  .withCreator(new DefaultCreatorsExample.MqListenerCreator)
                  .withCreator(new AutoCreator[DefaultCreatorsExample.Config, ParameterBase](classOf[DefaultCreatorsExample.Config]))
                  .build

    sys.ShutdownHookThread {
      service.close()
    }

    service.start()
  }

  // end::main[]
}
