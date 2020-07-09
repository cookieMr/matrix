package mr.cookie.matrix.listeners;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Measured elapsed time for each JUnit test and prints it after test execution regardless of status.
 * <p/>
 * This listener needs to be registered in {@code META-INF/services/org.junit.platform.launcher.TestExecutionListener}
 * file.
 */
public class JUnitElapsedTimeListener implements TestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(JUnitElapsedTimeListener.class);

    private static final ConcurrentHashMap<TestIdentifier, Long> ELAPSED_TIME_MAP =
            new ConcurrentHashMap<>();

    /**
     * Sets a start time into a map, where a key is a {@link TestIdentifier} object
     * and the value is current system time in nanoseconds.
     *
     * @param testIdentifier a test's identifier
     */
    @Override
    public void executionStarted(@NotNull TestIdentifier testIdentifier) {
        long start = System.nanoTime();
        ELAPSED_TIME_MAP.put(testIdentifier, start);
    }

    /**
     * Prints time elapsed for a test execution.
     *
     * @param testIdentifier      a test's identifier
     * @param testExecutionResult a test's execution result
     */
    @Override
    public void executionFinished(
            @NotNull TestIdentifier testIdentifier,
            @Nullable TestExecutionResult testExecutionResult) {
        long end = System.nanoTime();
        Long start = ELAPSED_TIME_MAP.remove(testIdentifier);

        if (start == null) {
            LOG.error("Test [{}] does not have a start time.", testIdentifier.getDisplayName());
            return;
        }

        if (LOG.isInfoEnabled()) {
            float elapsedTime = (float) (end - start) / 1000;
            LOG.info("Test [{}] took [{}] milli seconds.", testIdentifier.getDisplayName(), elapsedTime);
        }
    }

}
