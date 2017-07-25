package net.igsoft.sdi.internal;

public interface MapKeyGenerator {
    String generate(Class<?> clazz, String serializedParameters);
}
