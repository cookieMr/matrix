package mr.cookie.matrix.model;

import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

/**
 * A single thread implementations of an abstract {@link Matrix} class that represents a matrix.
 * Multiplication of two matrices is done in a single thread.
 *
 * @see Matrix
 */
public final class SingleThreadMatrix extends Matrix {

    /**
     * @see Matrix#Matrix(int, int, int...)
     */
    public SingleThreadMatrix(int rowSize, int columnSize, @NotNull int... elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * @see Matrix#Matrix(int, int, List)
     */
    public SingleThreadMatrix(int rowSize, int columnSize, @NotNull List<Integer> elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * Returns a new matrix that is a product of multiplying two input matrices. An input matrix
     * needs to have the same row count as the other matrix has column count (and vice versa).
     * If not then {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix that will be used during multiplication
     * @param m2 second matrix that will be used during multiplication
     * @return a new matrix that is a product of multiplying two matrices
     */
    public static @NotNull Matrix multiply(@NotNull Matrix m1, @NotNull Matrix m2) {
        verifyRowAndColumnCountsForMultiplication(m1, m2);

        int rowSize = m1.getRowSize();
        int[] numbers = new int[rowSize * rowSize];
        int index = 0;

        for (int r = 0; r < rowSize; r++) {
            for (int c = 0; c < rowSize; c++) {
                List<Integer> row = m1.getRow(r);
                List<Integer> column = m2.getColumn(c);

                numbers[index++] = IntStream.range(0, row.size())
                        .map(i -> row.get(i) * column.get(i))
                        .sum();
            }
        }

        return new SingleThreadMatrix(rowSize, rowSize, numbers);
    }

    /**
     * Returns a matrix with random row & column count and with random elements in it.<br/>
     * Row count is between 1 and 1000 (same goes for column count).<br/>
     * Elements value are between -100 and 100.
     *
     * @return a fully populated matrix
     */
    public static @NotNull SingleThreadMatrix random() {
        return random(1 + Random.nextInt(MAX_ALLOWED_SIZE), 1 + Random.nextInt(MAX_ALLOWED_SIZE));
    }

    /**
     * Returns a matrix with specified row & column counts with elements which have random
     * values (but in range of -100 and 100).
     *
     * @param rowCount    a row count for a matrix
     * @param columnCount a column count for a matrix
     * @return a fully populated matrix
     */
    public static @NotNull SingleThreadMatrix random(int rowCount, int columnCount) {
        int[] numbers = getRandomElements(rowCount * columnCount);
        return new SingleThreadMatrix(rowCount, columnCount, numbers);
    }

    @Override
    protected @NotNull SingleThreadMatrix minorMatrix(int row, int column) {
        int minorSize = getRowSize() - 1;
        return new SingleThreadMatrix(minorSize, minorSize, getMinorMatrixElements(row, column));
    }

}
