package net.igsoft.sdi;

import net.igsoft.sdi.testclasses.*;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoCreatorTest {
    private Service service;

    @Before
    public void setUp() {
        service = Service.builder()
                .withRootClass(F.class)
                .withCreator(new AutoCreator<>(Stepper.class))
                .withCreator(new AutoCreator<>(F.class))
                .withCreator(new AutoCreator<>(G.class))
                .withCreator(new AutoCreator<>(H.class))
                .build();
    }

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        assertThat(service.get(Stepper.class).toString()).isEqualTo("G:ctor H:ctor F:ctor");
    }
}
