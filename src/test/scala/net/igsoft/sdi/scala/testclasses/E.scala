package net.igsoft.sdi.scala.testclasses

class E(val stepper: Stepper) {
  stepper.addStep(this.getClass, "ctor")
}
