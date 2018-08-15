package net.igsoft.sdi.utils;

import java.util.Map;

import com.google.common.collect.Multimap;

import net.igsoft.sdi.creator.CreatorBase;
import net.igsoft.sdi.internal.Specification;

public final class LoggingUtils {

    public static String dependenciesByLevel(Multimap<Integer, String> instancesByLevel) {
        StringBuilder sb = new StringBuilder();

        for (Integer key : instancesByLevel.keySet()) {
            sb.append("Level ")
              .append(key)
              .append(": ")
              .append(instancesByLevel.get(key))
              .append('\n');
        }
        return sb.toString();
    }

    public static String dependenciesByClass(Map<String, Specification> instances) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Specification> entry : instances.entrySet()) {
            sb.append("Class '")
              .append(entry.getKey())
              .append("': ")
              .append(entry.getValue().getDependencies())
              .append('\n');
        }
        return sb.toString();
    }

    public static String unusedCreators(Map<Class<?>, CreatorBase<?, ?>> unusedCreators) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Class<?>, CreatorBase<?, ?>> entry : unusedCreators.entrySet()) {
            sb.append(entry.getValue().getClass().getSimpleName())
              .append(" (for class: ")
              .append(entry.getKey().getSimpleName())
              .append(")\n");
        }
        return sb.toString();
    }

    private LoggingUtils() {
    }
}
