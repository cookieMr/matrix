package mr.cookie.matrix.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SemaphoreMatrixTest {

    public static @NotNull Stream<ExecutorService> executorServices() {
        return ThreadPoolExecutorMatrixTest.executorServices();
    }

    @ParameterizedTest
    @MethodSource("executorServices")
    void customSemaphoreMultiply(@NotNull ExecutorService executor) {
        Matrix.setExecutor(executor);

        Matrix matrix1 = new SemaphoreMatrix(2, 3, 1, 0, 2, -1, 3, 1);
        Matrix matrix2 = new SemaphoreMatrix(3, 2, 3, 1, 2, 1, 1, 0);

        Matrix expected1 = new SingleThreadMatrix(2, 2, 5, 1, 4, 2);
        Matrix result1 = SemaphoreMatrix.multiply(matrix1, matrix2);
        assertThat(result1).isEqualTo(expected1);

        Matrix expected2 = new SingleThreadMatrix(3, 3, 2, 3, 7, 1, 3, 5, 1, 0, 2);
        Matrix result2 = SemaphoreMatrix.multiply(matrix2, matrix1);
        assertThat(result2).isEqualTo(expected2);

        executor.shutdown();
    }

    private static @NotNull Stream<Integer> exponentSizes() {
        return MatrixTest.exponentSizes();
    }

    @ParameterizedTest
    @MethodSource("exponentSizes")
    void customSemaphoreWithIncreasingSize(int size) {
        Matrix matrix = ThreadPoolExecutorMatrix.random(size, size);
        assertThatCode(() -> ThreadPoolExecutorMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

    private static @NotNull Stream<Arguments> mixedExecutorsAndSizes() {
        return MatrixTest.mixedExecutorsAndSizes();
    }

    @ParameterizedTest
    @MethodSource("mixedExecutorsAndSizes")
    void customSemaphoreWithIncreasingSize(@NotNull ExecutorService executor, int size) {
        Matrix.setExecutor(executor);

        Matrix matrix = SemaphoreMatrix.random(size, size);
        assertThatCode(() -> SemaphoreMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

    private static @NotNull Stream<Integer> semaphorePermits() {
        return Stream.of(10, 100, 300, 500, 700, 1_000);
    }

    @ParameterizedTest
    @MethodSource("semaphorePermits")
    void customSemaphoreWithIncreasingSizeOfPermits(int permits) {
        Matrix.setExecutor(Executors.newFixedThreadPool(1_000));
        Matrix.setSemaphorePermits(permits);

        Matrix matrix = SemaphoreMatrix.random(1_000, 1_000);
        assertThatCode(() -> SemaphoreMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

}
