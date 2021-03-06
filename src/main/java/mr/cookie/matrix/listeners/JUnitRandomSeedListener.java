package mr.cookie.matrix.listeners;

import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Resets {@link Random} seed before each JUnit tests to the same value. The seed value
 * is either specified by input parameter {@code -Dseed=long_value} or is generated from
 * {@link Random#nextLong()}.<br/>
 * <b>NOTE:</b> Each {@code @RepeatedTest} will have the same random see, thus it will
 * run with the same data, which defeats the purpose of running such repeated tests
 * while this listener is registered.
 * <p/>
 * This listener needs to be registered in {@code META-INF/services/org.junit.platform.launcher.TestExecutionListener}
 * file.
 */
public class JUnitRandomSeedListener implements TestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(JUnitRandomSeedListener.class);

    /**
     * The seed for pseudorandom generator. It's either taken from an application input parameter
     * {@code -Dseed=long_value} or generated by {@link Random#nextLong()}.
     */
    private static final long SEED = Optional.ofNullable(System.getProperty("seed", null))
            .map(Long::valueOf)
            .orElse(Random.nextLong());

    /**
     * Resets the random seed in {@link Random} before each JUnit test.
     * Each test is expected to have a the same seed, so the same (repeated) test will get
     * the same random data.
     *
     * @param testIdentifier a test's identifier
     */
    @Override
    public void executionStarted(@NotNull TestIdentifier testIdentifier) {
        LOG.debug("Test [{}] will be run with a seed [{}].", testIdentifier.getDisplayName(), SEED);
        Random.setSeed(SEED);
    }

    /**
     * Logs random seed for failed test on highest logging level.
     *
     * @param testIdentifier      a test's identifier
     * @param testExecutionResult a test's result
     */
    @Override
    public void executionFinished(
            @NotNull TestIdentifier testIdentifier,
            @NotNull TestExecutionResult testExecutionResult) {
        if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
            LOG.error("Test [{}] failed. Random seed was [{}].", testIdentifier.getDisplayName(), Random.getSeed());
        }
    }

}
