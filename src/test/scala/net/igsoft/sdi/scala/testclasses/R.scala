package net.igsoft.sdi.scala.testclasses

class R(val stepper: Stepper, val name: String, val surname: String) {
  stepper.addStep(this.getClass, "ctor(" + name + " " + surname + ")")
}
