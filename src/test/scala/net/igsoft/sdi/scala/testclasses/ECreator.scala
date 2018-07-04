package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.{Creator, InstanceCreator, LaunchType}

class ECreator extends Creator[E, LaunchType] {
  override def create(instanceCreator: InstanceCreator, launchType: LaunchType) = new E(instanceCreator
                                                                                        .getOrCreate(classOf[Stepper]))
}
