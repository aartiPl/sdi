package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.testclasses.PParametrizedCreator;
import net.igsoft.sdi.testclasses.RParametrizedCreator;
import net.igsoft.sdi.testclasses.Stepper;

public class ParametrizedCreatorTest {
    private Service service;

    @BeforeEach
    public void setUp() {
        service = Service.builder()
                         .withRootCreator(new PParametrizedCreator(),
                                          new PParametrizedCreator.Params(false, "id"))
                         .withCreator(new RParametrizedCreator())
                         .withCreator(new AutoCreator<>(Stepper.class))
                         .build();
    }

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "R:ctor(name surname) P:ctor(id r)");
    }
}
