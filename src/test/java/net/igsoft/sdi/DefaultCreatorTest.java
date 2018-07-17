package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.ParametrizedWithDefaultCreatorsCreator;
import net.igsoft.sdi.testclasses.ParametrizedWithDefaultCreatorsCreatorParams;
import net.igsoft.sdi.testclasses.Stepper;

public class DefaultCreatorTest {
    private Service service;

    @BeforeEach
    public void setUp() {
        service = Service.builder()
                         .withRootCreator(new ParametrizedWithDefaultCreatorsCreator(),
                                          new ParametrizedWithDefaultCreatorsCreatorParams("name",
                                                                                           "surname"))
                         .build();
    }

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        assertThat(service.get(Stepper.class).toString()).isEqualTo("E:ctor D:ctor R:ctor(name surname)");
    }
}
