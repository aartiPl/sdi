package net.igsoft.sdi;

import org.junit.jupiter.api.Test;

import net.igsoft.catalyst.testing.pojo.PojoTester;
import net.igsoft.sdi.engine.InstanceDescriptor;

public class PojoTest {
    @Test
    public void assertThatPojosAreCorrectlyDefined() {
        PojoTester.builder().withClass(InstanceDescriptor.class).build().execute();
    }
}
