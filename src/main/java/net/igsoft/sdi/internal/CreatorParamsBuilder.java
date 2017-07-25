package net.igsoft.sdi.internal;

import java.util.Map;

import com.google.common.collect.Maps;
import net.igsoft.sdi.CreatorParams;

public class CreatorParamsBuilder {

    private final Map<String, ParameterValue> params = Maps.newHashMap();

    public CreatorParamsBuilder parameter(String name, String param) {
        params.put(name, new ParameterValue(param, param));
        return this;
    }

    public CreatorParamsBuilder parameter(String name, Integer param) {
        params.put(name, new ParameterValue(param, Integer.toString(param)));
        return this;
    }

    public CreatorParamsBuilder parameter(String name, Long param) {
        params.put(name, new ParameterValue(param, Long.toString(param)));
        return this;
    }

    public CreatorParamsBuilder parameter(String name, Boolean param) {
        params.put(name, new ParameterValue(param, Boolean.toString(param)));
        return this;
    }

    public CreatorParamsBuilder parameter(String name, Class<?> param) {
        params.put(name, new ParameterValue(param, param.getName()));
        return this;
    }

    public CreatorParamsBuilder parameter(String name, Object param, String serialized) {
        params.put(name, new ParameterValue(param, serialized));
        return this;
    }

    public CreatorParams build() {
        return new CreatorParams(params);
    }
}
