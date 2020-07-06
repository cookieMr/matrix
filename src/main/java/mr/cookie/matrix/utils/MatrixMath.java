package mr.cookie.matrix.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MatrixMath {

    private MatrixMath() {
        throw new AssertionError("MatrixMath class should not be instantiated.");
    }

    @NotNull
    public static Matrix<Integer> add(@NotNull Matrix<Integer> m1, @NotNull Matrix<Integer> m2) {
        verifyRowCount(m1, m2);
        verifyColumnCount(m1, m2);

        final int rowSize = m1.getRowSize();
        final int columnSize = m1.getColumnSize();

        Integer[] numbers = new Integer[rowSize * columnSize];
        for (int c = 0; c < columnSize; c++) {
            List<Integer> rowM1 = m1.getRow(c);
            List<Integer> rowM2 = m2.getRow(c);
            for (int r = 0; r < rowSize; r++) {
                numbers[rowSize * c + r] = rowM1.get(r) + rowM2.get(r);
            }
        }

        return new Matrix<>(rowSize, columnSize, numbers);
    }

    @NotNull
    public static Matrix<Integer> subtract(@NotNull Matrix<Integer> m1, @NotNull Matrix<Integer> m2) {
        verifyRowCount(m1, m2);
        verifyColumnCount(m1, m2);

        final int rowSize = m1.getRowSize();
        final int columnSize = m1.getColumnSize();

        Integer[] numbers = new Integer[rowSize * columnSize];
        for (int c = 0; c < columnSize; c++) {
            List<Integer> rowM1 = m1.getRow(c);
            List<Integer> rowM2 = m2.getRow(c);
            for (int r = 0; r < rowSize; r++) {
                numbers[rowSize * c + r] = rowM1.get(r) - rowM2.get(r);
            }
        }

        return new Matrix<>(rowSize, columnSize, numbers);
    }

    private static void verifyRowCount(Matrix<?> m1, Matrix<?> m2) {
        if (m1.getRowSize() != m2.getRowSize()) {
            throw new IllegalArgumentException(
                    String.format("Both matrices must have the same row count. Provided sizes are [%d] and [%d]",
                            m1.getRowSize(), m2.getRowSize()));
        }
    }

    private static void verifyColumnCount(Matrix<?> m1, Matrix<?> m2) {
        if (m1.getColumnSize() != m2.getColumnSize()) {
            throw new IllegalArgumentException(
                    String.format("Both matrices must have the same column count. Provided sizes are [%d] and [%d]",
                            m1.getColumnSize(), m2.getColumnSize()));
        }
    }

}
