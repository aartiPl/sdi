package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import net.igsoft.sdi.testclasses.*;
import org.junit.Before;
import org.junit.Test;

public class ServiceLifecycleTest {

    /*
        Class hierarchy:
          1.          C
                     /\
          2.        A  \
                   /    \
          3.      B      B
                 |  \    | \
          4.     D   P   D  P
                 |       |
          5.     E       E
     */

    private Service service;

    @Before
    public void setUp() {
        service = Service.builder()
                         .withCreator(new ACreator())
                         .withCreator(new BCreator())
                         .withCreator(new CCreator())
                         .withCreator(new DCreator())
                         .withCreator(new ECreator())
                         .withCreator(new PCreator())
                         .withCreator(new RCreator())
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
    public void assertThatStartingServiceWithoutInitDoesntWork() {
        service.start();

        assertThat(service.get(Stepper.class).toString()).isEqualTo("E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor");
    }

    @Test
    public void assertThatStartingServiceWorks() {
        service.init();
        service.start();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init D:start B:start A:start C:start");
    }

    @Test
    public void assertThatClosingServiceWhichIsNotStartedDoesntWork() {
        service.close();

        assertThat(service.get(Stepper.class).toString()).isEqualTo("E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor");
    }

    @Test
    public void assertThatClosingServiceWorksInReverseOrder() {
        service.init();
        service.start();
        service.close();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname) P:ctor(id r) B:ctor A:ctor C:ctor D:init B:init A:init C:init D:start B:start A:start C:start C:stop A:stop B:stop D:stop C:close A:close B:close D:close");
    }
}
