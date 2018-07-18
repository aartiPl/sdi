package net.igsoft.sdi.testclasses;

import net.igsoft.sdi.ParameterBase;

public final class ParametrizedCreator1Params extends ParameterBase {

    private final String name;
    private final String surname;

    ParametrizedCreator1Params(String name, String surname) {
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
        return concatenate(name, surname);
    }
}
