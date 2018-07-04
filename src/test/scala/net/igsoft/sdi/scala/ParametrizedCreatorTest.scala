package net.igsoft.sdi.scala

import net.igsoft.sdi.{AutoCreator, ParametersBase, Service}
import net.igsoft.sdi.testclasses.P
import net.igsoft.sdi.testclasses.PCreator
import net.igsoft.sdi.testclasses.PCreatorParams
import net.igsoft.sdi.testclasses.RCreator
import net.igsoft.sdi.testclasses.Stepper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat


class ParametrizedCreatorTest {
  private var service : Service = _

  @BeforeEach def setUp(): Unit = {
    service = Service.builder.withRootClass(classOf[P], new PCreatorParams(false, "id")).withCreator(new PCreator).withCreator(new RCreator).withCreator(new AutoCreator[Stepper, ParametersBase](classOf[Stepper])).build
  }

  @Test def assertThatServiceIsBuiltCorrectly(): Unit = {
    assertThat(service.get(classOf[Stepper]).toString).isEqualTo("R:ctor(name surname) P:ctor(id r)")
  }
}
