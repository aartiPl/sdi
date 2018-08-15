package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import org.junit.jupiter.api.Test;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.testclasses.B;
import net.igsoft.sdi.testclasses.BCreator;
import net.igsoft.sdi.testclasses.E1Creator;
import net.igsoft.sdi.testclasses.ECreator;
import net.igsoft.sdi.testclasses.RWithDefaultCreatorsCreator;
import net.igsoft.sdi.testclasses.Stepper;

class CreationFailuresTest {
    @Test
    void assertThatGivingDuplicatedCreatorsCauseException() {
        //Given-When

        Throwable thrown = catchThrowable(() -> {
            Service.builder()
                   .withRootCreator(new ECreator())
                   .withCreator(new AutoCreator<>(Stepper.class))
                   .withCreator(new AutoCreator<>(Stepper.class))
                   .build();
        });

        //Then
        assertThat(thrown).isExactlyInstanceOf(IllegalArgumentException.class)
                          .hasMessageStartingWith("Duplicated creator given in");
    }

    @Test
    void assertThatDuplicatedDefaultCreatorsWithoutExplicitCreatorCauseException() {
        //Given-When

        Throwable thrown = catchThrowable(() -> {
            Service.builder()
                   .withCreator(new RWithDefaultCreatorsCreator())
                   .withCreator(new E1Creator())
                   .build();
        });

        //Then
        assertThat(thrown).isExactlyInstanceOf(IllegalStateException.class)
                          .hasMessageStartingWith("Found duplicated default creators");
    }

    @Test
    void assertThatThereIsAWarningAboutUnusedCreators() {
        //Given
        Service service = Service.builder()
                                 .withRootCreator(new ECreator())
                                 .withCreator(new AutoCreator<>(Stepper.class))
                                 .withCreator(new BCreator())
                                 .build();

        //When
        Throwable throwable = catchThrowable(() -> service.get(B.class));

        //Then
        assertThat(throwable).isExactlyInstanceOf(IllegalArgumentException.class)
                             .hasMessage("java.lang.IllegalArgumentException: There is no instance of class B available in Service");
        //TODO: add check logger output
    }
}
