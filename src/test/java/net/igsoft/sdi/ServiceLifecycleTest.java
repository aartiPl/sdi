package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.ACreator;
import net.igsoft.sdi.testclasses.BCreator;
import net.igsoft.sdi.testclasses.CCreator;
import net.igsoft.sdi.testclasses.DCreator;
import net.igsoft.sdi.testclasses.ECreator;
import net.igsoft.sdi.testclasses.RParametrizedCreator;
import net.igsoft.sdi.testclasses.PParametrizedCreator;
import net.igsoft.sdi.testclasses.Stepper;

public class ServiceLifecycleTest {

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

    private Service service;

    @BeforeEach
    public void setUp() {
        service = Service.builder()
                         .withRootCreator(new CCreator())
                         .withCreator(new ACreator())
                         .withCreator(new BCreator())
                         .withCreator(new DCreator())
                         .withCreator(new ECreator())
                         .withCreator(new PParametrizedCreator())
                         .withCreator(new RParametrizedCreator())
                         .withCreator(new AutoCreator<>(Stepper.class))
                         .build();
    }

    @Test
    public void assertThatClassesAreBuildAndInitIsCalledForManagedClasses() {
        service.init();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init");
    }

    @Test
    public void assertThatStartingServiceWithoutInitIsProperlyInitialized() {
        service.start();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init " +
                "D:start B:start A:start C:start");
    }

    @Test
    public void assertThatStartingServiceWorks() {
        service.init();
        service.start();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init " +
                "D:start B:start A:start C:start");
    }

    @Test
    public void assertThatClosingServiceWhichIsNotStartedDoesntWork() {
        service.close();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor");
    }

    @Test
    public void assertThatClosingServiceWorksInReverseOrder() {
        service.init();
        service.start();
        service.close();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init " +
                "D:start B:start A:start C:start C:stop A:stop B:stop D:stop C:close A:close B:close D:close");
    }
}
