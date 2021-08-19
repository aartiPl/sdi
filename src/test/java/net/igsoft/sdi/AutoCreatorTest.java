package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import org.junit.jupiter.api.Test;

import net.igsoft.sdi.creator.AutoCreator;
import net.igsoft.sdi.testclasses.FClass;
import net.igsoft.sdi.testclasses.GClass;
import net.igsoft.sdi.testclasses.HClass;
import net.igsoft.sdi.testclasses.PrivateCtrClass;
import net.igsoft.sdi.testclasses.QClass;
import net.igsoft.sdi.testclasses.RClass;
import net.igsoft.sdi.testclasses.RParametrizedCreator;
import net.igsoft.sdi.testclasses.Stepper;
import net.igsoft.sdi.testclasses.ThrowingCtrClass;

class AutoCreatorTest {

    @Test
    void assertThatServiceIsBuiltCorrectly() {
        //Given
        Service service = Service.builder()
                                 .withRootCreator(new AutoCreator<>(FClass.class))
                                 .withCreator(new AutoCreator<>(Stepper.class))
                                 .withCreator(new AutoCreator<>(GClass.class))
                                 .withCreator(new AutoCreator<>(HClass.class))
                                 .build();

        //When-Then
        assertThat(service.get(Stepper.class).toString()).isEqualTo("GClass:ctor HClass:ctor FClass:ctor");
    }

    @Test
    void assertThatAutoCreatorWithNotSingleConstructorThrows() {
        //Given-When
        Throwable thrown = catchThrowable(() -> Service.builder()
                                                   .withCreator(new AutoCreator<>(QClass.class))
                                                   .withCreator(new AutoCreator<>(Stepper.class))
                                                   .build());

        //Then
        assertThat(thrown).isExactlyInstanceOf(IllegalStateException.class)
                          .hasMessageStartingWith(
                                  "Class 'QClass' has more than one public constructor.");
    }

    @Test
    void assertThatUsingAutoCreatorWithClassWithPrivateConstructorThrowsException() {
        //Given-When
        Throwable thrown = catchThrowable(() -> Service.builder()
                                                   .withCreator(new AutoCreator<>(PrivateCtrClass.class))
                                                   .withCreator(new AutoCreator<>(Stepper.class))
                                                   .build());

        //Then
        assertThat(thrown).isExactlyInstanceOf(IllegalStateException.class)
                          .hasMessageStartingWith(
                                  "Can not automatically create class 'PrivateCtrClass'.");
    }

    @Test
    void assertThatUsingAutoCreatorWithClassWithThrowingConstructorThrowsException() {
        //Given-When
        Throwable thrown = catchThrowable(() -> Service.builder()
                                                   .withCreator(new AutoCreator<>(ThrowingCtrClass.class))
                                                   .withCreator(new AutoCreator<>(Stepper.class))
                                                   .build());

        //Then
        assertThat(thrown).isExactlyInstanceOf(IllegalStateException.class)
                          .hasMessageStartingWith(
                                  "Can not automatically create class 'ThrowingCtrClass' with parameters.");
    }
}
