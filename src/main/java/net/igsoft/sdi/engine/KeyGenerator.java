package net.igsoft.sdi.engine;

public interface KeyGenerator {
    String generate(Class<?> clazz, String serializedParameters);
}
