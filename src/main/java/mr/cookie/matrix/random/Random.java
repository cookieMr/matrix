package mr.cookie.matrix.random;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A proxy for {@link java.util.Random}.
 */
public final class Random {

    /**
     * A seed for this pseudo-random generator.
     */
    private static final AtomicLong seed = new AtomicLong(System.currentTimeMillis());

    /**
     * A Java's pseudo-random generator which is proxied by this class.
     */
    private static final AtomicReference<java.util.Random> random =
            new AtomicReference<>(new java.util.Random(seed.get()));

    /**
     * Returns a seed for this pseudo-random generator.
     *
     * @return a seed for this pseudo-random generator
     */
    public static long getSeed() {
        return seed.get();
    }

    public static void setSeed(long seed) {
        Random.seed.set(seed);
        Random.random.get().setSeed(seed);
    }

    /**
     * Returns the next pseudorandom, uniformly distributed int value from this random number generator's sequence.
     * All 2^32 possible {@code int} values are produced with (approximately) equal probability.
     *
     * @return a pseudo-random primitive integer
     */
    public static int nextInt() {
        return random.get().nextInt();
    }

    /**
     * Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value
     * (exclusive), drawn from this random number generator's sequence.
     *
     * @param bound upper bound
     * @return a pseudo-random primitive integer
     */
    public static int nextInt(int bound) {
        return random.get().nextInt(bound);
    }

    /**
     * Returns the next pseudorandom, uniformly distributed long value from this random number generator's sequence.
     * All 2^64 possible {@code long} values are produced with (approximately) equal probability.
     *
     * @return a pseudo-random primitive long
     */
    public static long nextLong() {
        return random.get().nextLong();
    }

    /**
     * Returns a pseudorandom, uniformly distributed long value between 0 (inclusive) and the specified value
     * (exclusive), drawn from this random number generator's sequence.
     *
     * @param bound upper bound
     * @return a pseudo-random primitive long
     */
    public static long nextInt(long bound) {
        return random.get().nextLong() % bound;
    }

}
