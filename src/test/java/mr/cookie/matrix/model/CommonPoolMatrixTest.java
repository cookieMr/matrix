package mr.cookie.matrix.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CommonPoolMatrixTest {

    @Test
    void commonPoolMultiply() throws ExecutionException, InterruptedException {
        Matrix matrix1 = new CommonPoolMatrix(2, 3, 1, 0, 2, -1, 3, 1);
        Matrix matrix2 = new CommonPoolMatrix(3, 2, 3, 1, 2, 1, 1, 0);

        Matrix expected1 = new SingleThreadMatrix(2, 2, 5, 1, 4, 2);
        Matrix result1 = CommonPoolMatrix.multiply(matrix1, matrix2);
        assertThat(result1).isEqualTo(expected1);

        Matrix expected2 = new SingleThreadMatrix(3, 3, 2, 3, 7, 1, 3, 5, 1, 0, 2);
        Matrix result2 = CommonPoolMatrix.multiply(matrix2, matrix1);
        assertThat(result2).isEqualTo(expected2);
    }

    private static @NotNull Stream<Integer> exponentSizes() {
        return MatrixTest.exponentSizes();
    }

    @ParameterizedTest
    @MethodSource("exponentSizes")
    void commonPoolMultiplyWithIncreasingSize(int size) {
        Matrix matrix = CommonPoolMatrix.random(size, size);
        assertThatCode(() -> CommonPoolMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

}