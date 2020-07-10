package mr.cookie.matrix.model;

import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An implementation of an abstract {@link Matrix} class with a configurable thread
 * pool of {@link ExecutorService} type. Multiplication of two matrices is spread
 * across available threads in a specified thread pool.
 *
 * @see Matrix
 */
public final class ThreadPoolExecutorMatrix extends Matrix {

    private static final AtomicReference<ExecutorService> EXECUTOR =
            new AtomicReference<>(Executors.newFixedThreadPool(10));

    /**
     * @see Matrix#Matrix(int, int, int...)
     */
    public ThreadPoolExecutorMatrix(int rowSize, int columnSize, @NotNull int... elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * @see Matrix#Matrix(int, int, List)
     */
    public ThreadPoolExecutorMatrix(int rowSize, int columnSize, @NotNull List<Integer> elements) {
        super(rowSize, columnSize, elements);
    }

    @Override
    protected @NotNull ThreadPoolExecutorMatrix minorMatrix(int row, int column) {
        int minorSize = getRowSize() - 1;
        return new ThreadPoolExecutorMatrix(minorSize, minorSize, getMinorMatrixElements(row, column));
    }

    /**
     * Returns a matrix with random row & column count and with random elements in it.<br/>
     * Row count is between 1 and 1000 (same goes for column count).<br/>
     * Elements value are between -100 and 100.
     *
     * @return a fully populated matrix
     */
    public static @NotNull ThreadPoolExecutorMatrix random() {
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
    public static @NotNull ThreadPoolExecutorMatrix random(int rowCount, int columnCount) {
        int[] numbers = getRandomElements(rowCount * columnCount);
        return new ThreadPoolExecutorMatrix(rowCount, columnCount, numbers);
    }

    /**
     * Sets a new executor to be used during matrices multiplication.
     *
     * @param executor a new executor to be used during matrices multiplication
     */
    public static void setExecutor(@NotNull ExecutorService executor) {
        EXECUTOR.set(executor);
    }

    /**
     * Returns a new matrix that is a product of multiplying two input matrices. An input matrix
     * needs to have the same row count as the other matrix has column count (and vice versa).
     * If not then {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix that will be used during multiplication
     * @param m2 second matrix that will be used during multiplication
     * @return a new matrix that is a product of multiplying two matrices
     * @throws InterruptedException if something goes wrong with multithreading
     * @throws ExecutionException   if something goes wrong with multithreading
     */
    public static @NotNull Matrix multiply(@NotNull Matrix m1, @NotNull Matrix m2)
            throws InterruptedException, ExecutionException {
        verifyRowAndColumnCountsForMultiplication(m1, m2);

        int rowSize = m1.getRowSize();
        List<MultiplyRowTask> tasks = IntStream.range(0, rowSize)
                .mapToObj(i -> new MultiplyRowTask(i, m1, m2))
                .collect(Collectors.toList());
        List<Future<List<Integer>>> futures = EXECUTOR.get().invokeAll(tasks);

        List<Integer> numbers = new ArrayList<>(rowSize * rowSize);
        for (Future<List<Integer>> row : futures) {
            numbers.addAll(row.get());
        }

        //TODO: i am confused... because of this line tests for multiplication run 18s
        // without it tests run 26s... why??? optimization??? but how???
        numbers.toArray(new Integer[rowSize * rowSize]);

        return new ThreadPoolExecutorMatrix(rowSize, rowSize, numbers);
    }

    /**
     * A private class calculating a single row for the resulting matrix. It multiplies
     * a specified row from the 1st {@link Matrix} with the whole 2nd {@link Matrix}.
     */
    private static class MultiplyRowTask implements Callable<List<Integer>> {

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

        @Override
        public List<Integer> call() {
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

}
