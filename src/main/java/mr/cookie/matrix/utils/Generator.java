package mr.cookie.matrix.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * An utility class that can generate random matrices.
 */
public final class Generator {

    private static final int MAX_ALLOWED_SIZE = 1000;
    private static final int MAX_ALLOWED_NUMBER = 100;
    private static final Random RANDOM = new Random();

    private Generator() {
        throw new AssertionError("Generator class should not be instantiated.");
    }

    /**
     * Returns a matrix with random row & column count and with random elements in it.<br/>
     * Row count is between 1 and 1000 (same goes for column count).<br/>
     * Elements value are between -100 and 100.
     *
     * @return a fully populated matrix
     */
    @NotNull
    public static Matrix random() {
        return random(1 + RANDOM.nextInt(MAX_ALLOWED_SIZE), 1 + RANDOM.nextInt(MAX_ALLOWED_SIZE));
    }

    /**
     * Returns a matrix with specified row & column counts with elements which have random
     * values (but in range of -100 and 100).
     *
     * @param rowCount    a row count for a matrix
     * @param columnCount a column count for a matrix
     * @return a fully populated matrix
     */
    @NotNull
    public static Matrix random(int rowCount, int columnCount) {
        int[] numbers = new int[rowCount * columnCount];

        int index = 0;
        while (index < rowCount * columnCount) {
            numbers[index] = MAX_ALLOWED_NUMBER - RANDOM.nextInt(2 * MAX_ALLOWED_NUMBER);
            index++;
        }

        return new Matrix(rowCount, columnCount, numbers);
    }

}
