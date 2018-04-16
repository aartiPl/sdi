package net.igsoft.sdi;

public abstract class Parameters {

    private String serialized = null;

    public String getSerialized() {
        if (serialized == null) {
            serialized = serialize();
        }

        return serialized;
    }

    protected String serialize() {

        //TODO: iterate over all fields in class (reflection):
        //<key>:<value>,....
        //based on: CreatorParamsBuilder.java
        //TODO: Cache this value on first use
        return "";
    }
}
