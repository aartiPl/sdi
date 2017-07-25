package net.igsoft.sdi.internal;

import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ParameterValue {
    private final Object value;
    private final String serializedValue;

    public ParameterValue(Object value, String serializedValue) {
        this.value = value;
        this.serializedValue = serializedValue;
    }

    public Object getValue() {
        return value;
    }

    public String getSerializedValue() {
        return serializedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParameterValue that = (ParameterValue) o;
        return Objects.equals(value, that.value) && Objects.equals(serializedValue, that.serializedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, serializedValue);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
