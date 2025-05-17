package pl.kondziet;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CommandType {
    ECHO, UNKNOWN;

    private static final Map<String, CommandType> LOOKUP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Enum::name, Function.identity()));

    public CommandType fromString(String name) {
        return LOOKUP.getOrDefault(name, UNKNOWN);
    }
}
