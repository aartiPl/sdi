package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.F;
import net.igsoft.sdi.testclasses.G;
import net.igsoft.sdi.testclasses.H;
import net.igsoft.sdi.testclasses.Stepper;

public class AutoCreatorTest {
    private Service service;

    @BeforeEach
    public void setUp() {
        service = Service.builder()
                         .withRootCreator(new AutoCreator<>(F.class))
                         .withCreator(new AutoCreator<>(Stepper.class))
                         .withCreator(new AutoCreator<>(G.class))
                         .withCreator(new AutoCreator<>(H.class))
                         .build();
    }

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        assertThat(service.get(Stepper.class).toString()).isEqualTo("G:ctor H:ctor F:ctor");
    }
}
