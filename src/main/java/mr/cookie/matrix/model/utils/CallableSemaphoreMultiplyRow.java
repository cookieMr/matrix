package mr.cookie.matrix.model.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public final class CallableSemaphoreMultiplyRow extends MultiplyRowTask implements Callable<List<Integer>> {

    private final Semaphore semaphore;

    /**
     * @param semaphore a semaphore to guard how many threads are concurrently
     *                  doing calculations for rows multiplication
     * @see MultiplyRowTask#MultiplyRowTask(int, Matrix, Matrix)
     */
    public CallableSemaphoreMultiplyRow(
            int rowIndex,
            @NotNull Matrix m1,
            @NotNull Matrix m2,
            @NotNull Semaphore semaphore) {
        super(rowIndex, m1, m2);
        this.semaphore = semaphore;
    }

    @Override
    public @NotNull List<Integer> call() {
        try {
            semaphore.acquire();
            return calculate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }

        throw new IllegalStateException("Acquired permit from semaphore was interrupted. " +
                "Cannot return a valid calculation result.");
    }

}
