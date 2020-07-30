package mr.cookie.matrix.executiontime;

import mr.cookie.matrix.model.CommonPoolMatrix;
import mr.cookie.matrix.model.CountDownLatchMatrix;
import mr.cookie.matrix.model.Matrix;
import mr.cookie.matrix.model.SemaphoreMatrix;
import mr.cookie.matrix.model.SingleThreadMatrix;
import mr.cookie.matrix.model.ThreadPoolExecutorMatrix;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ExecutionTimeTest {

    private static final BiFunction<Matrix, Matrix, Matrix> COMMON_POOL_MATRIX_MULTIPLY = CommonPoolMatrix::multiply;
    private static final BiFunction<Matrix, Matrix, Matrix> SINGLE_THREAD_MATRIX_MULTIPLY = SingleThreadMatrix::multiply;
    private static final BiFunction<Matrix, Matrix, Matrix> COUNT_DOWN_MATRIX_MULTIPLY = CountDownLatchMatrix::multiply;
    private static final BiFunction<Matrix, Matrix, Matrix> SEMAPHORE_MATRIX_MULTIPLY = SemaphoreMatrix::multiply;
    private static final BiFunction<Matrix, Matrix, Matrix> THREAD_POOL_MATRIX_MULTIPLY =
            ThreadPoolExecutorMatrix::multiply;

    private static final Matrix M1 = CountDownLatchMatrix.random(500, 500);
    private static final Matrix M2 = SemaphoreMatrix.random(500, 500);


    private static List<Arguments> nonPoolSizedArguments() {
        return Stream.of(
                Arguments.of(Executors.newCachedThreadPool(), 1, COUNT_DOWN_MATRIX_MULTIPLY, "CTP 1 Count Down"),
                Arguments.of(Executors.newWorkStealingPool(), 1, COUNT_DOWN_MATRIX_MULTIPLY, "WSP 1 Count Down"),
                Arguments.of(Executors.newSingleThreadExecutor(), 1, COUNT_DOWN_MATRIX_MULTIPLY, "STE 1 Count Down"),
                Arguments.of(Executors.newCachedThreadPool(), 1, THREAD_POOL_MATRIX_MULTIPLY, "CTP 1 Thread Pool"),
                Arguments.of(Executors.newWorkStealingPool(), 1, THREAD_POOL_MATRIX_MULTIPLY, "WSP 1 Thread Pool"),
                Arguments.of(Executors.newSingleThreadExecutor(), 1, THREAD_POOL_MATRIX_MULTIPLY, "STE 1 Thread Pool")
        ).collect(Collectors.toList());
    }


    private static List<Arguments> poolSizedArguments(int poolSize) {
        return Stream.of(
                Arguments.of(Executors.newFixedThreadPool(poolSize), 1, COUNT_DOWN_MATRIX_MULTIPLY,
                        String.format("FTP%d 1 Count Down", poolSize)),
                Arguments.of(Executors.newFixedThreadPool(poolSize), 1, THREAD_POOL_MATRIX_MULTIPLY,
                        String.format("FTP%d 1 Thread Pool", poolSize)),
                Arguments.of(Executors.newScheduledThreadPool(poolSize), 1, COUNT_DOWN_MATRIX_MULTIPLY,
                        String.format("STP%d 1 CountDown", poolSize)),
                Arguments.of(Executors.newScheduledThreadPool(poolSize), 1, THREAD_POOL_MATRIX_MULTIPLY,
                        String.format("STP%d 1 CountDown", poolSize))
        ).collect(Collectors.toList());
    }

    private static List<Arguments> nonPoolSizedSemaphoreArguments(int permits) {
        return Stream.of(
                Arguments.of(Executors.newCachedThreadPool(), permits, SEMAPHORE_MATRIX_MULTIPLY,
                        String.format("CTP %d Semaphore", permits)),
                Arguments.of(Executors.newWorkStealingPool(), permits, SEMAPHORE_MATRIX_MULTIPLY,
                        String.format("WSP %d Semaphore", permits))
        ).collect(Collectors.toList());
    }

    private static List<Arguments> poolSizedSemaphoreArguments(int poolSize, int permits) {
        return Stream.of(
                Arguments.of(Executors.newFixedThreadPool(poolSize), permits, SEMAPHORE_MATRIX_MULTIPLY,
                        String.format("FTP%d %d Semaphore", poolSize, permits)),
                Arguments.of(Executors.newScheduledThreadPool(poolSize), permits, SEMAPHORE_MATRIX_MULTIPLY,
                        String.format("STP%d %d Semaphore", poolSize, permits))
        ).collect(Collectors.toList());
    }

    private static List<Arguments> nonConfigurableArguments() {
        return Stream.of(
                Arguments.of(Executors.newSingleThreadExecutor(), 1, COMMON_POOL_MATRIX_MULTIPLY, "STE 1 Common Pool"),
                Arguments.of(Executors.newSingleThreadExecutor(), 1, SINGLE_THREAD_MATRIX_MULTIPLY,
                        "STE 1 Single Thread")
        ).collect(Collectors.toList());
    }

    private static Stream<Arguments> arguments() {
        int[] poolSizes = new int[]{1, 10, 100, 300, 500, 700};
        int[] permits = new int[]{1, 10, 100, 300, 500, 700};

        List<Arguments> poolSizedArguments = Arrays.stream(poolSizes)
                .mapToObj(ExecutionTimeTest::poolSizedArguments)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Arguments> nonPoolSizedSemaphoreArguments = Arrays.stream(permits)
                .mapToObj(ExecutionTimeTest::nonPoolSizedSemaphoreArguments)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Arguments> poolSizedSemaphoreArguments = Arrays.stream(permits)
                .mapToObj(permit -> Arrays.stream(poolSizes)
                        .mapToObj(poolSize -> poolSizedSemaphoreArguments(poolSize, permit))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return Stream.of(nonPoolSizedArguments(), nonConfigurableArguments(),
                poolSizedArguments, nonPoolSizedSemaphoreArguments, poolSizedSemaphoreArguments)
                .flatMap(Collection::stream);
    }

    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("arguments")
    void matrixMultiplication(
            @NotNull ExecutorService executorService,
            int permits,
            @NotNull BiFunction<Matrix, Matrix, Matrix> function,
            @SuppressWarnings("unused") @NotNull String testName) {
        Matrix.setExecutor(executorService);
        Matrix.setSemaphorePermits(permits);

        function.apply(M1, M2);
        executorService.shutdown();
    }

}
