package mr.cookie.matrix.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

/**
 * A math utility class that provides few useful matrices operation (e.g. addition, subtraction and multiplication)
 * along with input matrices verification (row & column count verification).
 */
public final class MatrixMath {

    private MatrixMath() {
        throw new AssertionError("MatrixMath class should not be instantiated.");
    }

    /**
     * Returns a new matrix that is a product of addition of two input matrices. Both matrices need to have the same
     * size (row count and column count). Otherwise {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix that will be a part of addition product
     * @param m2 second matrix that will be a part of addition product
     * @return a new matrix that is a product of addition of two matrices
     */
    @NotNull
    public static Matrix add(@NotNull Matrix m1, @NotNull Matrix m2) {
        verifyRowCount(m1, m2);
        verifyColumnCount(m1, m2);

        final int rowSize = m1.getRowSize();
        final int columnSize = m1.getColumnSize();

        int[] numbers = new int[rowSize * columnSize];
        for (int c = 0; c < columnSize; c++) {
            List<Integer> rowM1 = m1.getRow(c);
            List<Integer> rowM2 = m2.getRow(c);
            for (int r = 0; r < rowSize; r++) {
                numbers[rowSize * c + r] = rowM1.get(r) + rowM2.get(r);
            }
        }

        return new Matrix(rowSize, columnSize, numbers);
    }

    /**
     * Returns a new matrix that is a product of subtraction of two input matrices. Both matrices need to have the same
     * size (row count and column count). Otherwise {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix from which the other matrix will be subtracted
     * @param m2 second matrix which will be subtracted from the other matrix
     * @return a new matrix that is a product of subtraction of two matrices
     */
    @NotNull
    public static Matrix subtract(@NotNull Matrix m1, @NotNull Matrix m2) {
        verifyRowCount(m1, m2);
        verifyColumnCount(m1, m2);

        final int rowSize = m1.getRowSize();
        final int columnSize = m1.getColumnSize();

        int[] numbers = new int[rowSize * columnSize];
        for (int c = 0; c < columnSize; c++) {
            List<Integer> rowM1 = m1.getRow(c);
            List<Integer> rowM2 = m2.getRow(c);
            for (int r = 0; r < rowSize; r++) {
                numbers[rowSize * c + r] = rowM1.get(r) - rowM2.get(r);
            }
        }

        return new Matrix(rowSize, columnSize, numbers);
    }

    /**
     * Returns a new matrix that is a product of multiplying two input matrices. An input matrix needs to have
     * the same row count as the other matrix has column count (and vice versa). If not then
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix that will be used during multiplication
     * @param m2 second matrix that will be used during multiplication
     * @return a new matrix that is a product of multiplying two matrices
     */
    @NotNull
    public static Matrix multiply(@NotNull Matrix m1, @NotNull Matrix m2) {
        verifyRowAndColumnCountsForMultiplication(m1, m2);

        final int rowSize = m1.getRowSize();
        int[] numbers = new int[rowSize * rowSize];
        int index = 0;

        for (int c = 0; c < rowSize; c++) {
            for (int r = 0; r < rowSize; r++) {
                List<Integer> row = m1.getRow(c);
                List<Integer> column = m2.getColumn(r);

                numbers[index++] = IntStream.range(0, row.size())
                        .map(i -> row.get(i) * column.get(i))
                        .sum();
            }
        }

        return new Matrix(rowSize, rowSize, numbers);
    }

    /**
     * Verifies if first input matrix row count is the same as the second input matrix column count (and vice versa).
     * If not, then {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix which row & column count will be compared with the other matrix column & row count
     * @param m2 second matrix which column & row count will be compared with the other matrix row & column count
     */
    private static void verifyRowAndColumnCountsForMultiplication(@NotNull Matrix m1, @NotNull Matrix m2) {
        if (m1.getColumnSize() != m2.getRowSize()) {
            throw new IllegalArgumentException(String.format("These two matrices can not be multiplied. " +
                    "Column count [%d] and row count [%d] are not equal.", m1.getColumnSize(), m2.getRowSize()));
        }

        if (m1.getRowSize() != m2.getColumnSize()) {
            throw new IllegalArgumentException(String.format("These two matrices can not be multiplied. " +
                    "Row count [%d] and column count [%d] are not equal.", m1.getRowSize(), m2.getColumnSize()));
        }
    }

    /**
     * Verifies row counts for both input matrices. Row counts need to have the same value. If not then
     * {@link IllegalArgumentException} will be thrown with a proper message.
     *
     * @param m1 first matrix which row count will be compared with the other matrix
     * @param m2 second matrix which row count will be compared with the other matrix
     */
    private static void verifyRowCount(@NotNull Matrix m1, @NotNull Matrix m2) {
        if (m1.getRowSize() != m2.getRowSize()) {
            throw new IllegalArgumentException(
                    String.format("Both matrices must have the same row count. Provided sizes are [%d] and [%d]",
                            m1.getRowSize(), m2.getRowSize()));
        }
    }

    /**
     * Verifies column counts for both input matrices. Column counts need to have the same value. If not then
     * {@link IllegalArgumentException} will be thrown with a proper message.
     *
     * @param m1 first matrix which column count will be compared with the other matrix
     * @param m2 second matrix which column count will be compared with the other matrix
     */
    private static void verifyColumnCount(@NotNull Matrix m1, @NotNull Matrix m2) {
        if (m1.getColumnSize() != m2.getColumnSize()) {
            throw new IllegalArgumentException(
                    String.format("Both matrices must have the same column count. Provided sizes are [%d] and [%d]",
                            m1.getColumnSize(), m2.getColumnSize()));
        }
    }

}
