package pl.kondziet;

public sealed interface DataTypeParseResult {

    record Complete(DataType dataType) implements DataTypeParseResult {}
    record Incomplete() implements DataTypeParseResult {}
    record Error(String message) {}
}
