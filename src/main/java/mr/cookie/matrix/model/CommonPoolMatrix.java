package mr.cookie.matrix.model;

import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * An implementation of an abstract {@link Matrix} class with a common thread pool
 * ({@link ForkJoinPool#commonPool()}). Multiplication of two matrices is spread
 * across available threads in the common thread pool and each thread returns a
 * {@link Future}, which holds a single row for the resulting matrix.
 *
 * @see Matrix
 */
public final class CommonPoolMatrix extends Matrix {

    /**
     * @see Matrix#Matrix(int, int, int...)
     */
    public CommonPoolMatrix(int rowSize, int columnSize, @NotNull int... elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * @see Matrix#Matrix(int, int, List)
     */
    public CommonPoolMatrix(int rowSize, int columnSize, @NotNull List<Integer> elements) {
        super(rowSize, columnSize, elements);
    }

    @Override
    protected @NotNull CommonPoolMatrix minorMatrix(int row, int column) {
        int minorSize = getRowSize() - 1;
        return new CommonPoolMatrix(minorSize, minorSize, getMinorMatrixElements(row, column));
    }

    /**
     * Returns a matrix with random row & column count and with random elements in it.<br/>
     * Row count is between 1 and 1000 (same goes for column count).<br/>
     * Elements value are between -100 and 100.
     *
     * @return a fully populated matrix
     */
    public static @NotNull CommonPoolMatrix random() {
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
    public static @NotNull CommonPoolMatrix random(int rowCount, int columnCount) {
        int[] numbers = getRandomElements(rowCount * columnCount);
        return new CommonPoolMatrix(rowCount, columnCount, numbers);
    }

    /**
     * Returns a new matrix that is a product of multiplying two input matrices. An input
     * matrix needs to have the same row count as the other matrix has column count
     * (and vice versa). If not then {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix that will be used during multiplication
     * @param m2 second matrix that will be used during multiplication
     * @return a new matrix that is a product of multiplying two matrices
     * @throws ExecutionException   in case something went wrong while getting result
     *                              from a {@link Future#get()}, result of multiplication
     *                              cannot be obtained, so it's up to the used of
     *                              this method to handle such case
     * @throws InterruptedException in case something went wrong while getting result
     *                              from a {@link Future#get()}, result of multiplication
     *                              cannot be obtained, so it's up to the used of
     *                              this method to handle such case
     */
    public static @NotNull Matrix multiply(@NotNull Matrix m1, @NotNull Matrix m2)
            throws ExecutionException, InterruptedException {
        verifyRowAndColumnCountsForMultiplication(m1, m2);

        int rowSize = m1.getRowSize();

        List<Future<List<Integer>>> multipliedRows = new ArrayList<>();
        for (int r = 0; r < rowSize; r++) {
            multipliedRows.add(multiplyRow(r, m1, m2));
        }

        List<Integer> numbers = new ArrayList<>(rowSize * rowSize);
        for (Future<List<Integer>> row : multipliedRows) {
            numbers.addAll(row.get());
        }

        //TODO: i am confused... because of this line tests for multiplication run 3.5s
        // without it tests run 5s... why??? optimization??? but how???
        numbers.toArray(new Integer[rowSize * rowSize]);

        return new CommonPoolMatrix(rowSize, rowSize, numbers);
    }

    /**
     * Returns a {@link Future} that calculates a result row.
     *
     * @param rowIndex an index of a row, which will be multiplied by the 2nd matrix
     * @param m1       1st matrix to multiply
     * @param m2       2nd matrix to multiply
     * @return a future object, that will yield result of a specified row multiplied by the 2nd matrix
     */
    private static @NotNull Future<List<Integer>> multiplyRow(int rowIndex, @NotNull Matrix m1, @NotNull Matrix m2) {
        int rowSize = m1.getRowSize();

        return CompletableFuture.supplyAsync(() -> {
            List<Integer> resultingRow = new ArrayList<>();
            List<Integer> row = m1.getRow(rowIndex);

            for (int c = 0; c < rowSize; c++) {
                List<Integer> column = m2.getColumn(c);

                resultingRow.add(IntStream.range(0, row.size())
                        .map(i -> row.get(i) * column.get(i))
                        .sum());
            }

            return resultingRow;
        });
    }

}
