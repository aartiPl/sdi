package net.igsoft.sdi;

import net.igsoft.sdi.testclasses.PCreator;
import net.igsoft.sdi.testclasses.PCreatorParams;
import net.igsoft.sdi.testclasses.RCreator;
import net.igsoft.sdi.testclasses.Stepper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParametrizedCreatorTest {
    private Service service;

    @Before
    public void setUp() {
        service = Service.builder()
                .withCreator(new PCreator(), new PCreatorParams("id", false))
                .withCreator(new RCreator())
                .withCreator(new AutoCreator<>(Stepper.class))
                .build();
    }

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        assertThat(service.get(Stepper.class).toString()).isEqualTo("R:ctor(name surname) P:ctor(id r)");
    }
}
