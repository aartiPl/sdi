package net.igsoft.sdi.scala

import net.igsoft.sdi.testclasses._
import net.igsoft.sdi.{AutoCreator, ParameterBase, Service}
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.{BeforeEach, Test}

class ParametrizedCreatorTest {
  private var service: Service = _

  @BeforeEach def setUp(): Unit = {
    service = Service.builder
              .withRootCreator(new PParametrizedCreator, new PParametrizedCreatorParams(false, "id"))
              .withCreator(new RParametrizedCreator)
              .withCreator(new AutoCreator[Stepper, ParameterBase](classOf[Stepper]))
              .build
  }

  @Test def assertThatServiceIsBuiltCorrectly(): Unit = {
    assertThat(service.get(classOf[Stepper]).toString)
    .isEqualTo("R:ctor(name surname) P:ctor(id r)")
  }
}
