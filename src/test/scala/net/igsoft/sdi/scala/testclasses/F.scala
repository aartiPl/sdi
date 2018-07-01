package net.igsoft.sdi.scala.testclasses

class F(val stepper: Stepper, val g: G, val h: H) {
  stepper.addStep(this.getClass, "ctor")
}
