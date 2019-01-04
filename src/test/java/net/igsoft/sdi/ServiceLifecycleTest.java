package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.igsoft.sdi.creator.AutoCreator;
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
          1.          CClass
                     /\
          2.        AClass  \
                   /    \
          3.      BClass      BClass
                 |  \     | \
          4.     DClass   PClass    DClass  PClass
                 |     \   |   \
          5.     EClass     RClass    EClass    RClass
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
                "EClass:ctor DClass:ctor RClass:ctor(name surname) PClass:ctor(id r) BClass:ctor AClass:ctor CClass:ctor DClass:init BClass:init AClass:init CClass:init");
    }

    @Test
    public void assertThatStartingServiceWithoutInitIsProperlyInitialized() {
        service.start();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "EClass:ctor DClass:ctor RClass:ctor(name surname) PClass:ctor(id r) BClass:ctor AClass:ctor CClass:ctor DClass:init BClass:init AClass:init CClass:init " +
                "DClass:start BClass:start AClass:start CClass:start");
    }

    @Test
    public void assertThatStartingServiceWorks() {
        service.init();
        service.start();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "EClass:ctor DClass:ctor RClass:ctor(name surname) PClass:ctor(id r) BClass:ctor AClass:ctor CClass:ctor DClass:init BClass:init AClass:init CClass:init " +
                "DClass:start BClass:start AClass:start CClass:start");
    }

    @Test
    public void assertThatClosingServiceWhichIsNotStartedDoesntWork() {
        service.close();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "EClass:ctor DClass:ctor RClass:ctor(name surname) PClass:ctor(id r) BClass:ctor AClass:ctor CClass:ctor");
    }

    @Test
    public void assertThatClosingServiceWorksInReverseOrder() {
        service.init();
        service.start();
        service.close();

        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "EClass:ctor DClass:ctor RClass:ctor(name surname) PClass:ctor(id r) BClass:ctor AClass:ctor CClass:ctor DClass:init BClass:init AClass:init CClass:init " +
                "DClass:start BClass:start AClass:start CClass:start CClass:stop AClass:stop BClass:stop DClass:stop CClass:close AClass:close BClass:close DClass:close");
    }
}
