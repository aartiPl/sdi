package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.PCreator;
import net.igsoft.sdi.testclasses.PCreatorParams;
import net.igsoft.sdi.testclasses.RCreator;
import net.igsoft.sdi.testclasses.Stepper;

public class ParametrizedCreatorTest {
    private Service service;

    @BeforeEach
    public void setUp() {
        service = Service.builder()
                .withRootCreator(new PCreator(), new PCreatorParams(false, "id"))
                .withCreator(new RCreator())
                .withCreator(new AutoCreator<>(Stepper.class))
                .build();
    }

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        assertThat(service.get(Stepper.class).toString()).isEqualTo("R:ctor(name surname) P:ctor(id r)");
    }
}
