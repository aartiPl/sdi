package net.igsoft.sdi.internal;

public interface KeyGenerator {
    String generate(Class<?> clazz, String serializedParameters);
}
