package pl.kondziet;

public sealed interface Outcome<T> {

    record Success<T>(T value) implements Outcome<T> {}
    record Failure<T>(String message) implements Outcome<T> {}
    record Incomplete<T>() implements Outcome<T> {}
}
