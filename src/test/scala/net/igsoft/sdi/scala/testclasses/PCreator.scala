package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.{Creator, InstanceCreator, ParameterBase}

case class PCreatorParams(manualStartAndStop: Boolean, id: String) extends ParameterBase(manualStartAndStop) {
  override def uniqueId: String = id
}

class PCreator extends Creator[P, PCreatorParams] {
  override def create(instanceCreator: InstanceCreator, pCreatorParams: PCreatorParams): P = {
    val r = instanceCreator.getOrCreate(classOf[R], RCreatorParams("name", "surname"))

    new P(instanceCreator.getOrCreate(classOf[Stepper]), pCreatorParams.id, r)
  }
}
