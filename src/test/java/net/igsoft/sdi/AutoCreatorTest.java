package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import org.junit.jupiter.api.Test;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.testclasses.F;
import net.igsoft.sdi.testclasses.G;
import net.igsoft.sdi.testclasses.H;
import net.igsoft.sdi.testclasses.Q;
import net.igsoft.sdi.testclasses.Stepper;

public class AutoCreatorTest {

    @Test
    public void assertThatServiceIsBuiltCorrectly() {
        //Given
        Service service = Service.builder()
                         .withRootCreator(new AutoCreator<>(F.class))
                         .withCreator(new AutoCreator<>(Stepper.class))
                         .withCreator(new AutoCreator<>(G.class))
                         .withCreator(new AutoCreator<>(H.class))
                         .build();

        //When-Then
        assertThat(service.get(Stepper.class).toString()).isEqualTo("G:ctor H:ctor F:ctor");
    }

    @Test
    void assertThatAutoCreatorWithNotSingleConstructorThrows() {
        //Given-When
        Throwable thrown = catchThrowable(() -> {
            Service.builder()
                   .withCreator(new AutoCreator<>(Q.class))
                   .withCreator(new AutoCreator<>(Stepper.class))
                   .build();
        });

        //Then
        assertThat(thrown).isExactlyInstanceOf(IllegalStateException.class)
                          .hasMessageStartingWith("Class 'Q' has more than one public constructor.");
    }
}
