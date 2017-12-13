package net.igsoft.sdi.internal;

import java.util.Map;

import com.google.common.collect.Multimap;
import net.igsoft.sdi.Creator;

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

    public static String dependenciesByClass(Multimap<String, String> dependencies) {
        StringBuilder sb = new StringBuilder();
        for (String key : dependencies.keySet()) {
            sb.append("Class '")
              .append(key)
              .append("': ")
              .append(dependencies.get(key))
              .append('\n');
        }
        return sb.toString();
    }

    private LoggingUtils() {
    }

    public static String unusedCreators(Map<Class<?>, Creator<?>> unusedCreators) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Class<?>, Creator<?>> entry : unusedCreators.entrySet()) {
            sb.append(entry.getValue().getClass().getSimpleName())
              .append(" (for class: ")
              .append(entry.getKey().getSimpleName())
              .append(")\n");
        }
        return sb.toString();
    }
}
