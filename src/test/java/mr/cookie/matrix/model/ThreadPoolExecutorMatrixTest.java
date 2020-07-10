package mr.cookie.matrix.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ThreadPoolExecutorMatrixTest {

    private static @NotNull Stream<ExecutorService> executorServices() {
        return Stream.of(
                Executors.newFixedThreadPool(10),
                Executors.newCachedThreadPool(),
                Executors.newScheduledThreadPool(7),
                Executors.newWorkStealingPool(),
                Executors.newSingleThreadExecutor()
        );
    }

    @ParameterizedTest
    @MethodSource("executorServices")
    void customThreadPoolMultiply(@NotNull ExecutorService executor) throws ExecutionException, InterruptedException {
        ThreadPoolExecutorMatrix.setExecutor(executor);

        Matrix matrix1 = new ThreadPoolExecutorMatrix(2, 3, 1, 0, 2, -1, 3, 1);
        Matrix matrix2 = new ThreadPoolExecutorMatrix(3, 2, 3, 1, 2, 1, 1, 0);

        Matrix expected1 = new SingleThreadMatrix(2, 2, 5, 1, 4, 2);
        Matrix result1 = ThreadPoolExecutorMatrix.multiply(matrix1, matrix2);
        assertThat(result1).isEqualTo(expected1);

        Matrix expected2 = new SingleThreadMatrix(3, 3, 2, 3, 7, 1, 3, 5, 1, 0, 2);
        Matrix result2 = ThreadPoolExecutorMatrix.multiply(matrix2, matrix1);
        assertThat(result2).isEqualTo(expected2);
    }

    private static @NotNull Stream<Integer> exponentSizes() {
        return Stream.of(1, 10, 100, 1_000);
    }

    @ParameterizedTest
    @MethodSource("exponentSizes")
    void customThreadPoolMultiplyWithIncreasingSize(int size) {
        Matrix matrix = ThreadPoolExecutorMatrix.random(size, size);
        assertThatCode(() -> ThreadPoolExecutorMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

    private static @NotNull Stream<Arguments> mixedExecutorsAndSizes() {
        List<ExecutorService> executorServices = executorServices().collect(Collectors.toList());
        List<Integer> sizes = Arrays.asList(1, 10, 100);

        return executorServices.stream()
                .map(executor -> sizes.stream()
                        .map(size -> Arguments.of(executor, size))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream);
    }

    @ParameterizedTest
    @MethodSource("mixedExecutorsAndSizes")
    void customThreadPoolMultiplyWithIncreasingSize(ExecutorService executor, int size) {
        ThreadPoolExecutorMatrix.setExecutor(executor);

        Matrix matrix = ThreadPoolExecutorMatrix.random(size, size);
        assertThatCode(() -> CommonPoolMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

}
