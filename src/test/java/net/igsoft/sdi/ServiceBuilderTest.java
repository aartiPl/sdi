package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ServiceBuilderTest {

    /*
        Class hierarchy:
          1.          C
                     /\
          2.        A  \
                   /    \
          3.      B      B
                 |       |
          4.     D       D
                 |       |
          5.     E       E
     */

    private Stepper stepper;
    private Service service;

    @Before
    public void setUp() {
        stepper = new Stepper();
        service = Service.builder()
                         .withMainClass(C.class)
                         .withCreator(new ACreator(stepper))
                         .withCreator(new BCreator(stepper))
                         .withCreator(new CCreator(stepper))
                         .withCreator(new DCreator(stepper))
                         .withCreator(new ECreator(stepper))
                         .build();
    }

    @Test
    public void assertThatClassesAreBuildAndInitIsCalledForManagedClasses() {
        service.init();

        assertThat(stepper.toString()).isEqualTo(
                "E:ctor D:ctor B:ctor A:ctor C:ctor D:init B:init A:init C:init");
    }

    @Test
    public void assertThatStartingServiceWithoutInitDoesntWork() {
        service.start();

        assertThat(stepper.toString()).isEqualTo("E:ctor D:ctor B:ctor A:ctor C:ctor");
    }

    @Test
    public void assertThatStartingServiceWorks() {
        service.init();
        service.start();

        assertThat(stepper.toString()).isEqualTo(
                "E:ctor D:ctor B:ctor A:ctor C:ctor D:init B:init A:init C:init D:start B:start A:start C:start");
    }

    @Test
    public void assertThatClosingServiceWhichIsNotStartedDoesntWork() {
        service.close();

        assertThat(stepper.toString()).isEqualTo("E:ctor D:ctor B:ctor A:ctor C:ctor");
    }

    @Test
    public void assertThatClosingServiceWorksInReverseOrder() {
        service.init();
        service.start();
        service.close();

        assertThat(stepper.toString()).isEqualTo(
                "E:ctor D:ctor B:ctor A:ctor C:ctor D:init B:init A:init C:init D:start B:start A:start C:start C:stop A:stop B:stop D:stop C:close A:close B:close D:close");
    }
}
