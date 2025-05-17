package pl.kondziet;

public sealed interface ParseResult {

    record Complete(DataType dataType) implements ParseResult {}
    record Incomplete() implements ParseResult {}
    record Error(String message) {}
}
