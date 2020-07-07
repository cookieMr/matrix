package mr.cookie.matrix.utils;

import mr.cookie.matrix.model.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class Generator {

    private static final int MAX_ALLOWED_SIZE = 1000;
    private static final int MAX_ALLOWED_NUMBER = 100;
    private static final Random RANDOM = new Random();

    private Generator() {
        throw new AssertionError("Generator class should not be instantiated.");
    }

    @NotNull
    public static Matrix random() {
        return random(1 + RANDOM.nextInt(MAX_ALLOWED_SIZE), 1 + RANDOM.nextInt(MAX_ALLOWED_SIZE));
    }

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
