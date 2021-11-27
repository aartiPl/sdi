package net.igsoft.sdi.example

import com.google.common.collect.Lists
import net.igsoft.sdi.Service
import net.igsoft.sdi.creator.{AutoCreator, CreatorBase}
import net.igsoft.sdi.engine.InstanceProvider
import net.igsoft.sdi.parameter.{LaunchType, ParameterBase}

object DefaultCreatorsScalaExample {

  private[sdi] class Config

  // tag::default_creators[]
  private[sdi] class MqListenerWorker

  private[sdi] class MqListenerCreator extends CreatorBase[MqListener, LaunchType] {
    override def create(instanceProvider: InstanceProvider, params: LaunchType): MqListener = {
      val mqListenerWorker = instanceProvider.getOrCreate(classOf[MqListenerWorker])
      new MqListener(mqListenerWorker)
    }

    override def defaultCreators: java.util.List[CreatorBase[_, _ <: ParameterBase]] = Lists
                                                                                  .newArrayList(new AutoCreator[MqListenerWorker](classOf[MqListenerWorker]))
  }

  private[sdi] class MqListener(val mqListenerWorker: MqListenerWorker) {
  }

  // end::default_creators[]
  private[sdi] class App(val e: Config, val mqListener: MqListener) {
  }

  // tag::main[]
  def main(args: Array[String]): Unit = {
    val service = Service.builder
                  .withRootCreator(new AutoCreator(classOf[App]))
                  .withCreator(new MqListenerCreator)
                  .withCreator(new AutoCreator(classOf[Config]))
                  .build

    sys.ShutdownHookThread {
      service.close()
    }

    service.start()
  }

  // end::main[]
}
