package mr.cookie.matrix.model;

import mr.cookie.matrix.exceptions.MatrixInterruptedException;
import mr.cookie.matrix.model.utils.RunnableLatchMultiplyRowTask;
import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An implementation of an abstract {@link Matrix} class, where during matrices multiplication
 * a {@link CountDownLatch} is used to control when all worker threads finished.
 */
public final class CountDownLatchMatrix extends Matrix {

    /**
     * @see Matrix#Matrix(int, int, int...)
     */
    public CountDownLatchMatrix(int rowSize, int columnSize, @NotNull int... elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * @see Matrix#Matrix(int, int, List)
     */
    public CountDownLatchMatrix(int rowSize, int columnSize, @NotNull List<Integer> elements) {
        super(rowSize, columnSize, elements);
    }

    /**
     * Returns a matrix with random row & column count and with random elements in it.<br/>
     * Row count is between 1 and 1000 (same goes for column count).<br/>
     * Elements value are between -100 and 100.
     *
     * @return a fully populated matrix
     */
    public static @NotNull CountDownLatchMatrix random() {
        return random(1 + Random.nextInt(MAX_ALLOWED_SIZE), 1 + Random.nextInt(MAX_ALLOWED_SIZE));
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
        CountDownLatch latch = new CountDownLatch(rowSize);

        List<RunnableLatchMultiplyRowTask> rowTasks = IntStream.range(0, rowSize)
                .mapToObj(r -> new RunnableLatchMultiplyRowTask(r, m1, m2, latch))
                .collect(Collectors.toList());

        rowTasks.stream()
                .map(Thread::new)
                .forEach(Thread::start);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MatrixInterruptedException(e);
        }

        List<Integer> numbers = rowTasks.stream()
                .map(RunnableLatchMultiplyRowTask::getOutputRow)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new CountDownLatchMatrix(rowSize, rowSize, numbers);
    }

    /**
     * Returns a matrix with specified row & column counts with elements which have random
     * values (but in range of -100 and 100).
     *
     * @param rowCount    a row count for a matrix
     * @param columnCount a column count for a matrix
     * @return a fully populated matrix
     */
    public static @NotNull CountDownLatchMatrix random(int rowCount, int columnCount) {
        int[] numbers = getRandomElements(rowCount * columnCount);
        return new CountDownLatchMatrix(rowCount, columnCount, numbers);
    }

    @Override
    protected @NotNull CountDownLatchMatrix minorMatrix(int row, int column) {
        int minorSize = getRowSize() - 1;
        return new CountDownLatchMatrix(minorSize, minorSize, getMinorMatrixElements(row, column));
    }

}
