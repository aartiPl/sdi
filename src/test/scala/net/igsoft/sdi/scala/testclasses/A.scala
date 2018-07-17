package net.igsoft.sdi.scala.testclasses

import net.igsoft.sdi.Manageable

class A(val b: B, val stepper: Stepper) extends Manageable {
  stepper.addStep(this.getClass, "ctor")

  def hello: String = {
    stepper.addStep(this.getClass, "hello")
    "Hello World"
  }

  override def init(): Unit = {
    stepper.addStep(this.getClass, "init")
  }

  override def start(): Unit = {
    stepper.addStep(this.getClass, "start")
  }

  override def stop(): Unit = {
    stepper.addStep(this.getClass, "stop")
  }

  override def close(): Unit = {
    stepper.addStep(this.getClass, "close")
  }
}