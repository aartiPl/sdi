package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.ParametersBase;

public final class RCreatorParams extends ParametersBase {

    private final String name;
    private final String surname;

    RCreatorParams(String name, String surname, boolean manualStartAndStop) {
        super(manualStartAndStop);
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String uniqueId() {
        return name + surname;
    }
}
