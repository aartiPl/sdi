package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.{Creator, InstanceCreator, LaunchType}

class CCreator extends Creator[C, LaunchType] {
  override def create(instanceCreator: InstanceCreator, launchType: LaunchType): C = {
    val a = instanceCreator.getOrCreate(classOf[A], launchType)
    val b = instanceCreator.getOrCreate(classOf[B], launchType)
    new C(a, b, instanceCreator.getOrCreate(classOf[Stepper]))
  }
}
