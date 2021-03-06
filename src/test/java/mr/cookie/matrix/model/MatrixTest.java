package mr.cookie.matrix.model;

import mr.cookie.matrix.random.Random;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatrixTest {

    private static final Matrix MATRIX_2_BY_3 = new SingleThreadMatrix(2, 3, 1, 2, 3, 4, 5, 6);

    private static @NotNull Stream<Integer> invalidRowAndColumnSizes() {
        return Stream.of(-100, -1, 0);
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public static @NotNull Stream<ExecutorService> executorServices() {
        return Stream.of(
                Executors.newFixedThreadPool(CPU_COUNT),
                Executors.newFixedThreadPool(50),
                Executors.newFixedThreadPool(100),
                Executors.newCachedThreadPool(),
                Executors.newScheduledThreadPool(CPU_COUNT),
                Executors.newScheduledThreadPool(50),
                Executors.newScheduledThreadPool(100),
                Executors.newWorkStealingPool(),
                Executors.newSingleThreadExecutor()
        );
    }

    public static @NotNull Stream<Integer> exponentSizes() {
        return Stream.of(1, 10, 100, 1_000);
    }

    public static @NotNull Stream<Arguments> mixedExecutorsAndSizes() {
        List<Integer> sizes = Arrays.asList(1, 10, 100);

        return executorServices()
                .map(executor -> sizes.stream()
                        .map(size -> Arguments.of(executor, size))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream);
    }

    private static @NotNull Stream<Integer> disallowedPermits() {
        return Stream.of(Integer.MIN_VALUE, -100, -1, 0);
    }

    @ParameterizedTest
    @MethodSource("disallowedPermits")
    void throwsIfPermitsNumberIsLessThanOne(int permits) {
        assertThatThrownBy(() -> Matrix.setSemaphorePermits(permits))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Count of permits for semaphore must be a positive number.");
    }

    private static @NotNull Stream<Integer> allowedPermits() {
        return Stream.of(1, 10, 100, Integer.MAX_VALUE);
    }

    @ParameterizedTest
    @MethodSource("allowedPermits")
    void doesNotThrowIfPermitsNumberIsGreaterThanZero(int permits) {
        assertThatCode(() -> Matrix.setSemaphorePermits(permits)).doesNotThrowAnyException();
    }

    @Test
    void invalidRowSizesWhileAddingThrowException() {
        Matrix m1 = SingleThreadMatrix.random(1, 2);
        Matrix m2 = SingleThreadMatrix.random(2, 2);

        assertThatThrownBy(() -> Matrix.add(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same row count. Provided sizes are [%d] and [%d]", 1, 2);
        assertThatThrownBy(() -> Matrix.subtract(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same row count. Provided sizes are [%d] and [%d]", 1, 2);
    }

    @Test
    void invalidColumnSizesWhileAddingThrowException() {
        Matrix m1 = SingleThreadMatrix.random(2, 1);
        Matrix m2 = SingleThreadMatrix.random(2, 2);

        assertThatThrownBy(() -> Matrix.add(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same column count. Provided sizes are [%d] and [%d]", 1, 2);
        assertThatThrownBy(() -> Matrix.subtract(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same column count. Provided sizes are [%d] and [%d]", 1, 2);
    }

    @Test
    void add() {
        Matrix matrix1 = new SingleThreadMatrix(2, 2, 1, 2, 3, 4);
        Matrix matrix2 = new SingleThreadMatrix(2, 2, 4, 3, 2, 1);
        Matrix result = Matrix.add(matrix1, matrix2);

        Assertions.assertAll(
                () -> assertThat(result.getRowSize()).isEqualTo(matrix1.getRowSize()),
                () -> assertThat(result.getColumnSize()).isEqualTo(matrix1.getColumnSize()),
                () -> assertThat(result.get(0, 0)).isEqualTo(5),
                () -> assertThat(result.get(1, 1)).isEqualTo(5),
                () -> assertThat(result).isEqualTo(new SingleThreadMatrix(2, 2, 5, 5, 5, 5))
        );
    }

    @Test
    void subtract() {
        Matrix matrix1 = new SingleThreadMatrix(2, 2, 1, 2, 3, 4);
        Matrix matrix2 = new SingleThreadMatrix(2, 2, 4, 3, 2, 1);
        Matrix result = Matrix.subtract(matrix1, matrix2);

        Assertions.assertAll(
                () -> assertThat(result.getRowSize()).isEqualTo(matrix1.getRowSize()),
                () -> assertThat(result.getColumnSize()).isEqualTo(matrix1.getColumnSize()),
                () -> assertThat(result.get(0, 0)).isEqualTo(-3),
                () -> assertThat(result.get(1, 1)).isEqualTo(3),
                () -> assertThat(result).isEqualTo(new SingleThreadMatrix(2, 2, -3, -1, 1, 3))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRowAndColumnSizes")
    void constructorRowCountCannotBeTooSmall(int rowCount) {
        assertThatThrownBy(() -> new SingleThreadMatrix(rowCount, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Row size must be greater than 0, it was [%d].", rowCount);
    }

    @ParameterizedTest
    @MethodSource("invalidRowAndColumnSizes")
    void constructorColumnCountCannotBeTooSmall(int columnCount) {
        assertThatThrownBy(() -> new SingleThreadMatrix(1, columnCount, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column size must be greater than 0, it was [%d].", columnCount);
    }

    @Test
    void constructorHasTooFewNumbersProvided() {
        assertThatThrownBy(() -> new SingleThreadMatrix(2, 2, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Integers must have length [%d], but the input has length of [%d].", 4, 1);
    }

    @Test
    void constructorHasTooManyNumbersProvided() {
        assertThatThrownBy(() -> new SingleThreadMatrix(2, 2, 1, 2, 3, 4, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Integers must have length [%d], but the input has length of [%d].", 4, 5);
    }

    @Test
    void getRowAndColumnSize() {
        Matrix matrix = new SingleThreadMatrix(2, 1, 1, 2);

        assertThat(matrix.getRowSize()).isEqualTo(2);
        assertThat(matrix.getColumnSize()).isEqualTo(1);
    }

    @Test
    void get() {
        assertThat(MATRIX_2_BY_3.get(0, 0)).isEqualTo(1);
        assertThat(MATRIX_2_BY_3.get(1, 1)).isEqualTo(4);
    }

    private static @NotNull Stream<Integer> invalidRowColumnIndexes() {
        return Stream.of(-100, -1, 3, 100);
    }

    @ParameterizedTest
    @MethodSource("invalidRowColumnIndexes")
    void getByInvalidRow(int row) {
        assertThatThrownBy(() -> MATRIX_2_BY_3.get(row, 1))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("Row index is [%d] while row count is [%d].", row, MATRIX_2_BY_3.getRowSize());
    }

    @ParameterizedTest
    @MethodSource("invalidRowColumnIndexes")
    void getByInvalidColumn(int column) {
        assertThatThrownBy(() -> MATRIX_2_BY_3.get(1, column))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("Column index is [%d] while column count is [%d].",
                        column, MATRIX_2_BY_3.getColumnSize());
    }

    @Test
    void getRow() {
        assertThat(MATRIX_2_BY_3.getRow(1))
                .hasSize(3)
                .hasSize(MATRIX_2_BY_3.getColumnSize())
                .containsExactly(4, 5, 6);
    }

    @Test
    void getColumn() {
        assertThat(MATRIX_2_BY_3.getColumn(1))
                .hasSize(2)
                .hasSize(MATRIX_2_BY_3.getRowSize())
                .containsExactly(2, 5);
    }

    @Test
    void isSquared() {
        assertThat(SingleThreadMatrix.random(1, 2).isSquared()).isFalse();
        assertThat(SingleThreadMatrix.random(20, 20).isSquared()).isTrue();
    }

    private static @NotNull Stream<Integer> constants() {
        return Stream.of(-100, -1, 0, 1, 2, 99);
    }

    @ParameterizedTest
    @MethodSource("constants")
    void multiplyByConstant(int constant) {
        Matrix matrix = new SingleThreadMatrix(2, 2, 1, 2, 3, 4);
        matrix.multiplyByConstant(constant);

        assertThat(matrix.get(0, 0)).isEqualTo(constant);
        assertThat(matrix.get(1, 1)).isEqualTo(4 * constant);
    }

    @Test
    void equalsAndHashCodeContractsForAllMatrixImplementation() {
        int[] primitiveIntegers = IntStream.range(1, 10).toArray();
        List<Integer> boxedIntegers = IntStream.range(1, 10)
                .boxed()
                .collect(Collectors.toList());

        Collection<Matrix> matrices = Arrays.asList(
                new SingleThreadMatrix(3, 3, primitiveIntegers),
                new SingleThreadMatrix(3, 3, boxedIntegers),
                new CommonPoolMatrix(3, 3, primitiveIntegers),
                new CommonPoolMatrix(3, 3, boxedIntegers),
                new ThreadPoolExecutorMatrix(3, 3, primitiveIntegers),
                new ThreadPoolExecutorMatrix(3, 3, boxedIntegers),
                new CountDownLatchMatrix(3, 3, primitiveIntegers),
                new CountDownLatchMatrix(3, 3, boxedIntegers),
                new SemaphoreMatrix(3, 3, primitiveIntegers),
                new SemaphoreMatrix(3, 3, boxedIntegers)
        );

        for (Matrix matrix : matrices) {
            assertThat(matrices)
                    .allMatch(m -> m.hashCode() == matrix.hashCode())
                    .allMatch(matrix::equals)
                    .allMatch(m -> m.getDeterminant() == matrix.getDeterminant());
        }
    }

    @Test
    void equalsAndHashcodeContractsForRandomMatrices() {
        final long seed = Random.getSeed();
        Collection<Matrix> matrices = new ArrayList<>();

        Random.setSeed(seed);
        matrices.add(SingleThreadMatrix.random());
        Random.setSeed(seed);
        matrices.add(CommonPoolMatrix.random());
        Random.setSeed(seed);
        matrices.add(ThreadPoolExecutorMatrix.random());
        Random.setSeed(seed);
        matrices.add(CountDownLatchMatrix.random());
        Random.setSeed(seed);
        matrices.add(SemaphoreMatrix.random());

        for (Matrix matrix : matrices) {
            assertThat(matrices)
                    .allMatch(m -> m.hashCode() == matrix.hashCode())
                    .allMatch(matrix::equals);
        }
    }

    @Test
    void equalsAndHashcodeContractsForRandomWithSizeMatrices() {
        final long seed = Random.getSeed();
        Collection<Matrix> matrices = new ArrayList<>();

        Random.setSeed(seed);
        matrices.add(SingleThreadMatrix.random(100, 100));
        Random.setSeed(seed);
        matrices.add(CommonPoolMatrix.random(100, 100));
        Random.setSeed(seed);
        matrices.add(ThreadPoolExecutorMatrix.random(100, 100));
        Random.setSeed(seed);
        matrices.add(CountDownLatchMatrix.random(100, 100));
        Random.setSeed(seed);
        matrices.add(SemaphoreMatrix.random(100, 100));

        for (Matrix matrix : matrices) {
            assertThat(matrices)
                    .allMatch(m -> m.hashCode() == matrix.hashCode())
                    .allMatch(matrix::equals);
        }
    }

    @Test
    void notEqualObjects() {
        Matrix matrix = CommonPoolMatrix.random();
        Collection<Object> objects = Arrays.asList(null, new Object(), CommonPoolMatrix.random());

        assertThat(objects).noneMatch(matrix::equals);
    }

}
