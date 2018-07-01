package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.{Creator, InstanceCreator, ParametersBase}

case class RCreatorParams private[testclasses](name: String, surname: String) extends ParametersBase {
  override def uniqueId: String = name + surname
}

class RCreator extends Creator[R, RCreatorParams] {
  override def create(instanceCreator: InstanceCreator, rCreatorParams: RCreatorParams) = new R(instanceCreator.getOrCreate(classOf[Stepper]), rCreatorParams.name, rCreatorParams.surname)
}
