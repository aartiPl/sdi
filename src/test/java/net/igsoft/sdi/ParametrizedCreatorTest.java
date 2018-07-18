package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.ParametrizedCreator1;
import net.igsoft.sdi.testclasses.ParametrizedCreator2;
import net.igsoft.sdi.testclasses.ParametrizedCreator2Params;
import net.igsoft.sdi.testclasses.Stepper;

public class ParametrizedCreatorTest {
    private Service service;

    @BeforeEach
    public void setUp() {
        service = Service.builder()
                         .withRootCreator(new ParametrizedCreator2(),
                                          new ParametrizedCreator2Params(false, "id"))
                         .withCreator(new ParametrizedCreator1())
                         .withCreator(new AutoCreator<>(Stepper.class))
                         .build();
    }

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "R:ctor(name surname) P:ctor(id r)");
    }
}
