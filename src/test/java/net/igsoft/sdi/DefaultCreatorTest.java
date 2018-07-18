package net.igsoft.sdi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.igsoft.sdi.testclasses.Stepper;
import net.igsoft.sdi.testclasses.WithDefaultCreatorsCreator;
import net.igsoft.sdi.testclasses.WithDefaultCreatorsCreatorParams;

class DefaultCreatorTest {

    private static final WithDefaultCreatorsCreator CREATOR = new WithDefaultCreatorsCreator();
    private static final WithDefaultCreatorsCreatorParams CREATOR_PARAMETER =
            new WithDefaultCreatorsCreatorParams("name", "surname");

    @Test
    void assertThatAllCreatorsCanBeExtractedFromDefaultCreators() {
        Service service = Service.builder().withRootCreator(CREATOR, CREATOR_PARAMETER).build();
        assertThat(service.get(Stepper.class).toString()).isEqualTo(
                "E:ctor D:ctor R:ctor(name surname)");
    }
}
