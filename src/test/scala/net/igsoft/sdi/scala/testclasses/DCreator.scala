package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.Creator
import net.igsoft.sdi.InstanceCreator
import net.igsoft.sdi.LaunchType


class DCreator extends Creator[D, LaunchType] {
  override def create(instanceCreator: InstanceCreator, launchType: LaunchType): D = {
    val e = instanceCreator.getOrCreate(classOf[E], launchType)
    new D(e, instanceCreator.getOrCreate(classOf[Stepper]))
  }
}
