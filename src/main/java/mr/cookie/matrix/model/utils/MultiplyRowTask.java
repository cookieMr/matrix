package mr.cookie.matrix.model.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * An abstract class calculating a single row for the resulting matrix. It multiplies
 * a specified row from the 1st {@link Matrix} with the whole 2nd {@link Matrix}.
 */
public abstract class MultiplyRowTask {

    private final int rowIndex;
    private final Matrix m1;
    private final Matrix m2;

    /**
     * @param rowIndex index of the row to be multiplied
     * @param m1       source matrix of the row
     * @param m2       a matrix which will be used to multiply the row
     */
    public MultiplyRowTask(int rowIndex, @NotNull Matrix m1, @NotNull Matrix m2) {
        this.rowIndex = rowIndex;
        this.m1 = m1;
        this.m2 = m2;
    }

    protected @NotNull List<Integer> calculate() {
        int rowSize = m1.getRowSize();

        List<Integer> resultingRow = new ArrayList<>();
        List<Integer> row = m1.getRow(rowIndex);

        for (int c = 0; c < rowSize; c++) {
            List<Integer> column = m2.getColumn(c);

            resultingRow.add(IntStream.range(0, row.size())
                    .map(i -> row.get(i) * column.get(i))
                    .sum());
        }

        return resultingRow;
    }

}
