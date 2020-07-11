package mr.cookie.matrix.model.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A {@link Callable} class calculating a single row for the resulting matrix. It multiplies
 * a specified row from the 1st {@link Matrix} with the whole 2nd {@link Matrix}.
 */
public final class CallableMultiplyRowTask extends MultiplyRowTask implements Callable<List<Integer>> {

    /**
     * @see MultiplyRowTask#MultiplyRowTask(int, Matrix, Matrix)
     */
    public CallableMultiplyRowTask(int rowIndex, @NotNull Matrix m1, @NotNull Matrix m2) {
        super(rowIndex, m1, m2);
    }

    @Override
    public @NotNull List<Integer> call() {
        return calculate();
    }

}
