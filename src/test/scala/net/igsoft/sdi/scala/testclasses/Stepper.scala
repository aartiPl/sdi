package net.igsoft.sdi.scala.testclasses

import scala.collection.mutable

class Stepper() {
  val steps : mutable.Buffer[String] = mutable.Buffer()

  def addStep(clazz: Class[_], step: String): Unit = {
    steps += clazz.getSimpleName + ":" + step
  }

  override def toString: String = steps.mkString(" ")
}
