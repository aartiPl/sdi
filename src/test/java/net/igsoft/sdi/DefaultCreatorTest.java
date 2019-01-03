package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.RWithDefaultCreatorsCreator;
import net.igsoft.sdi.testclasses.Stepper;

class DefaultCreatorTest {

    private static final RWithDefaultCreatorsCreator CREATOR = new RWithDefaultCreatorsCreator();
    private static final RWithDefaultCreatorsCreator.Params CREATOR_PARAMETER =
            new RWithDefaultCreatorsCreator.Params("name", "surname");

    @Test
    void assertThatAllCreatorsCanBeExtractedFromDefaultCreators() {
        Service service = Service.builder().withRootCreator(CREATOR, CREATOR_PARAMETER).build();
        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname)");
    }
}
