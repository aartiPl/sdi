package net.igsoft.sdi.scala

import net.igsoft.sdi.{AutoCreator, ParametersBase, Service}
import net.igsoft.sdi.scala.testclasses._
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.{BeforeEach, Test}


class AutoCreatorTest {
  private var service: Service = _

  @BeforeEach
  def setUp(): Unit = {
    service = Service.builder
      .withRootClass(classOf[F])
      .withCreator(new AutoCreator[Stepper, ParametersBase](classOf[Stepper]))
      .withCreator(new AutoCreator[F, ParametersBase](classOf[F]))
      .withCreator(new AutoCreator[G, ParametersBase](classOf[G]))
      .withCreator(new AutoCreator[H, ParametersBase](classOf[H]))
      .build
  }

  @Test
  def assertThatServiceIsBuiltCorrectly(): Unit = {
    assertThat(service.get(classOf[Stepper]).toString).isEqualTo("G:ctor H:ctor F:ctor")
  }
}
