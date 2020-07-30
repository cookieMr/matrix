package mr.cookie.matrix.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ThreadPoolExecutorMatrixTest {

    public static @NotNull Stream<ExecutorService> executorServices() {
        return MatrixTest.executorServices();
    }

    @ParameterizedTest
    @MethodSource("executorServices")
    void customThreadPoolMultiply(@NotNull ExecutorService executor) {
        Matrix.setExecutor(executor);

        Matrix matrix1 = new ThreadPoolExecutorMatrix(2, 3, 1, 0, 2, -1, 3, 1);
        Matrix matrix2 = new ThreadPoolExecutorMatrix(3, 2, 3, 1, 2, 1, 1, 0);

        Matrix expected1 = new SingleThreadMatrix(2, 2, 5, 1, 4, 2);
        Matrix result1 = ThreadPoolExecutorMatrix.multiply(matrix1, matrix2);
        assertThat(result1).isEqualTo(expected1);

        Matrix expected2 = new SingleThreadMatrix(3, 3, 2, 3, 7, 1, 3, 5, 1, 0, 2);
        Matrix result2 = ThreadPoolExecutorMatrix.multiply(matrix2, matrix1);
        assertThat(result2).isEqualTo(expected2);

        executor.shutdown();
    }

    private static @NotNull Stream<Integer> exponentSizes() {
        return MatrixTest.exponentSizes();
    }

    @ParameterizedTest
    @MethodSource("exponentSizes")
    void customThreadPoolMultiplyWithIncreasingSize(int size) {
        Matrix matrix = ThreadPoolExecutorMatrix.random(size, size);
        assertThatCode(() -> ThreadPoolExecutorMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

    private static @NotNull Stream<Arguments> mixedExecutorsAndSizes() {
        return MatrixTest.mixedExecutorsAndSizes();
    }

    @ParameterizedTest
    @MethodSource("mixedExecutorsAndSizes")
    void customThreadPoolMultiplyWithIncreasingSize(@NotNull ExecutorService executor, int size) {
        Matrix.setExecutor(executor);

        Matrix matrix = ThreadPoolExecutorMatrix.random(size, size);
        assertThatCode(() -> ThreadPoolExecutorMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();

        executor.shutdown();
    }

}
