package mr.cookie.matrix.random;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A proxy for {@link java.util.Random}.
 */
public final class Random {

    private Random() {
        throw new UnsupportedOperationException("Random class should never be instantiated.");
    }

    /**
     * A seed for this pseudo-random generator.
     */
    private static final AtomicLong SEED = new AtomicLong(System.currentTimeMillis());

    /**
     * A Java's pseudo-random generator which is proxied by this class.<br/>
     * <b>Note:</b> This Pseudo-random number generator (PRNG) is not secure.
     * It reproduces "random" numbers on purpose!
     */
    @SuppressWarnings("squid:S2245")
    private static final AtomicReference<java.util.Random> PSEUDO_RANDOM_GENERATOR =
            new AtomicReference<>(new java.util.Random(SEED.get()));

    /**
     * Returns a seed for this pseudo-random generator.
     *
     * @return a seed for this pseudo-random generator
     */
    public static long getSeed() {
        return SEED.get();
    }

    /**
     * Sets a seed and resets pseudo-random generator's seed to provided value.
     *
     * @param seed a new seed
     */
    public static void setSeed(long seed) {
        Random.SEED.set(seed);
        Random.PSEUDO_RANDOM_GENERATOR.get().setSeed(seed);
    }

    /**
     * Returns the next pseudorandom, uniformly distributed int value from this random number generator's sequence.
     * All 2^32 possible {@code int} values are produced with (approximately) equal probability.
     *
     * @return a pseudo-random primitive integer
     */
    public static int nextInt() {
        return PSEUDO_RANDOM_GENERATOR.get().nextInt();
    }

    /**
     * Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value
     * (exclusive), drawn from this random number generator's sequence.
     *
     * @param bound upper bound
     * @return a pseudo-random primitive integer
     */
    public static int nextInt(int bound) {
        return PSEUDO_RANDOM_GENERATOR.get().nextInt(bound);
    }

    /**
     * Returns the next pseudorandom, uniformly distributed long value from this random number generator's sequence.
     * All 2^64 possible {@code long} values are produced with (approximately) equal probability.
     *
     * @return a pseudo-random primitive long
     */
    public static long nextLong() {
        return PSEUDO_RANDOM_GENERATOR.get().nextLong();
    }

    /**
     * Returns a pseudorandom, uniformly distributed long value between 0 (inclusive) and the specified value
     * (exclusive), drawn from this random number generator's sequence.
     *
     * @param bound upper bound
     * @return a pseudo-random primitive long
     */
    public static long nextInt(long bound) {
        return PSEUDO_RANDOM_GENERATOR.get().nextLong() % bound;
    }

}
