package net.igsoft.sdi;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.igsoft.sdi.internal.CreatorParamsBuilder;
import net.igsoft.sdi.internal.ParameterValue;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CreatorParams {

    public static final CreatorParams EMPTY_PARAMS = new CreatorParams(Maps.newHashMap());

    private final Map<String, ParameterValue> params;
    private final String serializedParameters;
    private final Set<String> parameterSet;

    public CreatorParams(Map<String, ParameterValue> params) {
        checkNotNull(params);
        this.params = params;
        this.serializedParameters = params.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> e.getKey() + ":" + e.getValue().getSerializedValue())
                .reduce((f, s) -> f + "," + s)
                .orElse("");
        this.parameterSet = Sets.newHashSet(params.keySet());
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String name) {
        if (!params.containsKey(name)) {
            throw new IllegalArgumentException("Creator parameter '" + name + "' does not exist.");
        }

        parameterSet.remove(name);

        return (T) params.get(name).getValue();
    }

    public String getSerializedParameters() {
        return serializedParameters;
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public boolean areAllUsed() {
        return parameterSet.isEmpty();
    }

    public String unusedParameters() {
        return parameterSet.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreatorParams creatorParams = (CreatorParams) o;
        return Objects.equals(params, creatorParams.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

    public static CreatorParamsBuilder builder() {
        return new CreatorParamsBuilder();
    }
}
