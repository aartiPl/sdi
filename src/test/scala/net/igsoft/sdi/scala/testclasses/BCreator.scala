package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.{Creator, InstanceCreator, LaunchType}

class BCreator extends Creator[B, LaunchType] {
  override def create(instanceCreator: InstanceCreator, launchType: LaunchType): B = {
    val d = instanceCreator.getOrCreate(classOf[D], launchType)
    val p = instanceCreator.getOrCreate(classOf[P], new PCreatorParams(false, "id"))
    new B(d, p, instanceCreator.getOrCreate(classOf[Stepper]))
  }
}
