package mr.cookie.matrix.random;

import mr.cookie.matrix.model.Matrix;

import java.util.Random;

public class Generator {

    private static final int MAX_ALLOWED_SIZE = 1000;
    private static final Random RANDOM = new Random();

    private Generator() {
        throw new AssertionError("Generator class should not be instantiated.");
    }

    public static Matrix random() {
        return random(1 + RANDOM.nextInt(MAX_ALLOWED_SIZE), 1 + RANDOM.nextInt(MAX_ALLOWED_SIZE));
    }

    public static Matrix random(int rowCount, int columnCount) {
        return null;
    }

}
