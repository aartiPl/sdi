package net.igsoft.sdi.scala.testclasses

class G(val stepper: Stepper) {
  stepper.addStep(this.getClass, "ctor")
}
