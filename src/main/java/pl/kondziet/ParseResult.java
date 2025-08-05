package pl.kondziet;

public sealed interface ParseResult {

    record Complete(RespType element) implements ParseResult {}
    record Incomplete() implements ParseResult {}
}
