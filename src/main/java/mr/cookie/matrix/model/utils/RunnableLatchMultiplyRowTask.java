package mr.cookie.matrix.model.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A class implementing {@link Runnable} to calculate a single row for the resulting matrix.
 * It multiplies a specified row from the 1st {@link Matrix} with the whole 2nd {@link Matrix}.
 * This class uses a {@link CountDownLatch} to notify parent thread if it finished a task.
 */
public final class RunnableLatchMultiplyRowTask extends MultiplyRowTask implements Runnable {

    private @Nullable List<Integer> outputRow;
    private final CountDownLatch latch;

    /**
     * @param latch a latch to tell parent thread if this worker i finished with a task
     * @see MultiplyRowTask#MultiplyRowTask(int, Matrix, Matrix)
     */
    public RunnableLatchMultiplyRowTask(
            int rowIndex,
            @NotNull Matrix m1,
            @NotNull Matrix m2,
            @NotNull CountDownLatch latch) {
        super(rowIndex, m1, m2);
        this.latch = latch;
    }

    @Override
    public void run() {
        outputRow = calculate();
        latch.countDown();
    }

    /**
     * Returns an output row of multiplication.
     *
     * @return an output row of multiplication
     */
    public @NotNull List<Integer> getOutputRow() {
        if (latch.getCount() != 0) {
            throw new IllegalStateException(String.format(
                    "Latch did not count down to 0 yet. Its counter is [%d].", latch.getCount()));
        }
        if (outputRow == null) {
            throw new IllegalStateException("This task was not run before calling this getter.");
        }
        return outputRow;
    }

}
