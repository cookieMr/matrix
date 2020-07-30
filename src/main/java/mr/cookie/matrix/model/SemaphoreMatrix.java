package mr.cookie.matrix.model;

import mr.cookie.matrix.exceptions.MatrixInterruptedException;
import mr.cookie.matrix.model.utils.CallableSemaphoreMultiplyRow;
import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SemaphoreMatrix extends Matrix {

    /**
     * @see Matrix#Matrix(int, int, int...)
     */
    public SemaphoreMatrix(int rowSize, int columnSize, @NotNull int... elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * @see Matrix#Matrix(int, int, List)
     */
    public SemaphoreMatrix(int rowSize, int columnSize, @NotNull List<Integer> elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * Returns a matrix with specified row & column counts with elements which have random
     * values (but in range of -100 and 100).
     *
     * @param rowCount    a row count for a matrix
     * @param columnCount a column count for a matrix
     * @return a fully populated matrix
     */
    public static @NotNull SemaphoreMatrix random(int rowCount, int columnCount) {
        int[] numbers = getRandomElements(rowCount * columnCount);
        return new SemaphoreMatrix(rowCount, columnCount, numbers);
    }

    /**
     * Returns a matrix with random row & column count and with random elements in it.<br/>
     * Row count is between 1 and 1000 (same goes for column count).<br/>
     * Elements value are between -100 and 100.
     *
     * @return a fully populated matrix
     */
    public static @NotNull SemaphoreMatrix random() {
        return random(1 + Random.nextInt(MAX_ALLOWED_SIZE), 1 + Random.nextInt(MAX_ALLOWED_SIZE));
    }

    @Override
    protected @NotNull SemaphoreMatrix minorMatrix(int row, int column) {
        int minorSize = getRowSize() - 1;
        return new SemaphoreMatrix(minorSize, minorSize, getMinorMatrixElements(row, column));
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
        Semaphore semaphore = SEMAPHORE.get();
        List<CallableSemaphoreMultiplyRow> tasks = IntStream.range(0, rowSize)
                .mapToObj(i -> new CallableSemaphoreMultiplyRow(i, m1, m2, semaphore))
                .collect(Collectors.toList());
        try {
            List<Future<List<Integer>>> futures = EXECUTOR.get().invokeAll(tasks);

            List<Integer> numbers = new ArrayList<>(rowSize * rowSize);
            for (Future<List<Integer>> row : futures) {
                numbers.addAll(row.get());
            }
            return new ThreadPoolExecutorMatrix(rowSize, rowSize, numbers);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new MatrixInterruptedException(e);
        }
    }

}
