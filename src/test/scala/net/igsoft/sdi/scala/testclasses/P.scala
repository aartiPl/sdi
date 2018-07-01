package net.igsoft.sdi.scala.testclasses

class P(val stepper: Stepper, val id: String, val r: R) {
  stepper.addStep(this.getClass, "ctor(" + id + " r)")
}
