package mr.cookie.matrix.model;

import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

/**
 * A single thread implementations of a Matrix class that represents a matrix.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Matrix_(mathematics)">Wikipedia - Matrix (mathematics)</a>
 */
public final class SingleThreadMatrix extends Matrix {

    /**
     * Constructs new matrix with specified size and specified elements. Verifies if row & column sizes are
     * greater than zero and that input elements can fill up whole matrix (their size is exactly
     * {@code rowSize * columnSize}). If not then {@link IllegalArgumentException} with a proper message is thrown.
     *
     * @param rowSize    a number of rows in the new matrix (must be greater than zero)
     * @param columnSize a number of column in the new matrix (must be greater than zero)
     * @param elements   elements of the new matrix, it needs to be the length of {@code rowSize * columnSize}
     */
    public SingleThreadMatrix(int rowSize, int columnSize, @NotNull int... elements) {
        super(rowSize, columnSize, elements);
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
    public static @NotNull Matrix multiply(@NotNull Matrix m1, @NotNull Matrix m2) {
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
        int[] numbers = new int[rowCount * columnCount];

        int index = 0;
        while (index < rowCount * columnCount) {
            numbers[index] = MAX_ALLOWED_NUMBER - Random.nextInt(2 * MAX_ALLOWED_NUMBER);
            index++;
        }

        return new SingleThreadMatrix(rowCount, columnCount, numbers);
    }

    @Override
    public int getDeterminant() {
        if (!isSquared()) {
            throw new UnsupportedOperationException(String.format(
                    "This matrix is not squared, thus it has no determinant. Its dimensions are [%dx%d].",
                    getRowSize(), getColumnSize()));
        }

        if (determinant == null) {
            determinant = calculateDeterminant();
        }

        return determinant;
    }

    @Override
    protected int calculateDeterminant() {
        if (getRowSize() == 1) {
            return get(0, 0);
        }

        if (getRowSize() == 2) {
            return get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1);
        }

        int sum = 0;
        for (int c = 0; c < getRowSize(); c++) {
            int s = get(0, c) * minorMatrix(0, c).getDeterminant();
            if (c % 2 == 0) {
                sum += s;
            } else {
                sum -= s;
            }
        }

        return sum;
    }

    @Override
    protected @NotNull SingleThreadMatrix minorMatrix(int row, int column) {
        final int minorSize = getRowSize() - 1;
        int[] minorNumber = new int[minorSize * minorSize];
        int index = 0;

        for (int c = 0; c < getRowSize(); c++) {
            if (c == column) {
                continue;
            }
            for (int r = 0; r < getRowSize(); r++) {
                if (r == row) {
                    continue;
                }
                minorNumber[index++] = get(r, c);
            }
        }

        return new SingleThreadMatrix(minorSize, minorSize, minorNumber);
    }

}
