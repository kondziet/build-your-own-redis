# build-your-own-redis

The project is built on a single-threaded Java NIO event loop using selectors, channels, and byte buffers. I could have used virtual threads, but the event loop approach is closer to the original Redis implementation in C and is also more performant, avoiding the heap overhead of virtual thread continuations.
