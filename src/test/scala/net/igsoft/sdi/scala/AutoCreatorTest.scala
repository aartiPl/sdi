package net.igsoft.sdi.scala

import net.igsoft.sdi.{AutoCreator, LaunchType, ParameterBase, Service}
import net.igsoft.sdi.scala.testclasses._
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.{BeforeEach, Test}


class AutoCreatorTest {
  private var service: Service = _

  @BeforeEach
  def setUp(): Unit = {
    service = Service.builder
              .withRootCreator(new AutoCreator[F, LaunchType](classOf[F]))
              .withCreator(new AutoCreator[Stepper, ParameterBase](classOf[Stepper]))
              .withCreator(new AutoCreator[G, ParameterBase](classOf[G]))
              .withCreator(new AutoCreator[H, ParameterBase](classOf[H]))
              .build
  }

  @Test
  def assertThatServiceIsBuiltCorrectly(): Unit = {
    assertThat(service.get(classOf[Stepper]).toString).isEqualTo("G:ctor H:ctor F:ctor")
  }
}
