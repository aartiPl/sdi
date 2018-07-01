package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.Creator
import net.igsoft.sdi.InstanceCreator
import net.igsoft.sdi.LaunchType


class CCreator extends Creator[C, LaunchType] {
  override def create(instanceCreator: InstanceCreator, launchType: LaunchType): C = {
    val a = instanceCreator.getOrCreate(classOf[A], launchType)
    val b = instanceCreator.getOrCreate(classOf[B], launchType)
    new C(a, b, instanceCreator.getOrCreate(classOf[Stepper]))
  }
}
