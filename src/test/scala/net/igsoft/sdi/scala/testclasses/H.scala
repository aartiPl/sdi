package net.igsoft.sdi.scala.testclasses

class H(val stepper: Stepper, val g: G) {
  stepper.addStep(this.getClass, "ctor")
}
