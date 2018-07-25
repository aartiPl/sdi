package net.igsoft.sdi.scala

import net.igsoft.sdi.testclasses._
import net.igsoft.sdi.{AutoCreator, ParameterBase, Service}
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.{BeforeEach, Test}


class ServiceLifecycleTest {
  /*
         Class hierarchy:
           1.          C
                      /\
           2.        A  \
                    /    \
           3.      B      B
                  |  \     | \
           4.     D   P    D  P
                  |     \   |   \
           5.     E     R    E    R
      */

  private var service: Service = _

  @BeforeEach def setUp(): Unit = {
    service = Service.builder
              .withRootCreator(new CCreator)
              .withCreator(new ACreator)
              .withCreator(new BCreator)
              .withCreator(new DCreator)
              .withCreator(new ECreator)
              .withCreator(new PParametrizedCreator)
              .withCreator(new RParametrizedCreator)
              .withCreator(new AutoCreator[Stepper, ParameterBase](classOf[Stepper]))
              .build
  }

  @Test def assertThatClassesAreBuildAndInitIsCalledForManagedClasses(): Unit = {
    service.init()
    assertThat(service.get(classOf[Stepper]).toString)
    .isEqualTo("E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init")
  }

  @Test def assertThatStartingServiceWithoutInitDoesntWork(): Unit = {
    service.start()
    assertThat(service.get(classOf[Stepper]).toString)
      .isEqualTo("E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init "
        + "D:start B:start A:start C:start")
  }

  @Test def assertThatStartingServiceWorks(): Unit = {
    service.init()
    service.start()
    assertThat(service.get(classOf[Stepper]).toString)
    .isEqualTo("E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init "
      + "D:start B:start A:start C:start")
  }

  @Test def assertThatClosingServiceWhichIsNotStartedDoesntWork(): Unit = {
    service.close()
    assertThat(service.get(classOf[Stepper]).toString)
    .isEqualTo("E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor")
  }

  @Test def assertThatClosingServiceWorksInReverseOrder(): Unit = {
    service.init()
    service.start()
    service.close()
    assertThat(service.get(classOf[Stepper]).toString)
    .isEqualTo("E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init " + "D:start B:start A:start C:start C:stop A:stop B:stop D:stop C:close A:close B:close D:close")
  }
}
