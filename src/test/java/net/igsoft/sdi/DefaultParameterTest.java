package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.RWithDefaultCreatorsCreator;
import net.igsoft.sdi.testclasses.Stepper;

class DefaultParameterTest {

    private static final RWithDefaultCreatorsCreator CREATOR = new RWithDefaultCreatorsCreator();
    private static final RWithDefaultCreatorsCreator.Params CREATOR_PARAMETER =
            new RWithDefaultCreatorsCreator.Params("name", "surname");

    @Test
    void assertThatDefaultRootCreatorParametersAreConsidered() {
        Service service = Service.builder().withRootCreator(CREATOR, CREATOR_PARAMETER).build();
        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "EClass:ctor DClass:ctor RClass:ctor(name surname)");
    }

    @Test
    void assertThatDefaultCreatorParametersAreConsidered() {
        Service service = Service.builder().withCreator(CREATOR, CREATOR_PARAMETER).build();
        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "EClass:ctor DClass:ctor RClass:ctor(name surname)");
    }
}
