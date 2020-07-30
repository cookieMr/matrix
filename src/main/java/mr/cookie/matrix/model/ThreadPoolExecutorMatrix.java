package mr.cookie.matrix.model;

import mr.cookie.matrix.exceptions.MatrixInterruptedException;
import mr.cookie.matrix.model.utils.CallableMultiplyRowTask;
import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
        List<CallableMultiplyRowTask> tasks = IntStream.range(0, rowSize)
                .mapToObj(i -> new CallableMultiplyRowTask(i, m1, m2))
                .collect(Collectors.toList());
        try {
            List<Future<List<Integer>>> futures = EXECUTOR.get().invokeAll(tasks);

            List<Integer> numbers = new ArrayList<>(rowSize * rowSize);
            for (Future<List<Integer>> row : futures) {
                numbers.addAll(row.get());
            }

            //TODO: i am confused... because of this line tests for multiplication run 18s
            // without it tests run 26s... why??? optimization??? but how???
            numbers.toArray(new Integer[rowSize * rowSize]);

            return new ThreadPoolExecutorMatrix(rowSize, rowSize, numbers);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new MatrixInterruptedException(e);
        }
    }

}
