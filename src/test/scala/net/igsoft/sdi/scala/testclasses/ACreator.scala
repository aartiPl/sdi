package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.Creator
import net.igsoft.sdi.InstanceCreator
import net.igsoft.sdi.LaunchType


class ACreator extends Creator[A, LaunchType] {
  override def create(instanceCreator: InstanceCreator, launchType: LaunchType): A = {
    val b = instanceCreator.getOrCreate(classOf[B], launchType)
    new A(b, instanceCreator.getOrCreate(classOf[Stepper]))
  }
}
