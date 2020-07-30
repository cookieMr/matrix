package mr.cookie.matrix.model;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SingleThreadMatrixTest {

    private static @NotNull Stream<Integer> matrixSizes() {
        return Stream.of(1, 10, 100, 1000);
    }

    @ParameterizedTest
    @MethodSource("matrixSizes")
    void randomRowSize(int size) {
        Matrix matrix = SingleThreadMatrix.random(size, 2);

        assertThat(matrix.getRowSize())
                .isEqualTo(size);
    }

    @ParameterizedTest
    @MethodSource("matrixSizes")
    void randomColumnSize(int size) {
        Matrix matrix = SingleThreadMatrix.random(2, size);

        assertThat(matrix.getColumnSize())
                .isEqualTo(size);
    }

    @Test
    void randomMatrixGeneration() {
        Matrix matrix = SingleThreadMatrix.random();

        assertThat(matrix.getColumnSize())
                .isPositive()
                .isLessThanOrEqualTo(1000);
        assertThat(matrix.getRowSize())
                .isPositive()
                .isLessThanOrEqualTo(1000);
    }

    @Test
    void nonSquaredMatrixThrowsExceptionWhenGettingDeterminant() {
        Matrix matrix = SingleThreadMatrix.random(2, 3);

        assertThatThrownBy(matrix::getDeterminant)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("This matrix is not squared, thus it has no determinant. Its dimensions are [%dx%d].",
                        2, 3);
    }

    private static @NotNull Stream<Arguments> matricesAndDeterminants() {
        return Stream.of(
                Arguments.of(new SingleThreadMatrix(1, 1, 10), 10),
                Arguments.of(new SingleThreadMatrix(2, 2, 1, 2, 3, 4), -2),
                Arguments.of(new SingleThreadMatrix(2, 2, 0, 0, 100, 100), 0),
                Arguments.of(new SingleThreadMatrix(3, 3, 0, 0, 0, 100, 100, 100, 100, 100, 100), 0),
                Arguments.of(new SingleThreadMatrix(4, 4, 0, 1, 2, 7, 1, 2, 3, 4, 5, 6, 7, 8, -1, 1, -1, 1), -64),
                Arguments.of(new SingleThreadMatrix(4, 4, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("matricesAndDeterminants")
    void getDeterminant(Matrix matrix, int determinant) {
        assertThat(matrix.getDeterminant()).isEqualTo(determinant);
    }

    @Test
    void firstConditionOfRowAndColumnCheckThrowsException() {
        Matrix m1 = SingleThreadMatrix.random(2, 3);
        Matrix m2 = SingleThreadMatrix.random(2, 3);

        assertThatThrownBy(() -> SingleThreadMatrix.multiply(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("These two matrices can not be multiplied. " +
                        "Column count [%d] and row count [%d] are not equal.", 3, 2);
    }

    @Test
    void secondConditionOfRowAndColumnCheckThrowsException() {
        Matrix m1 = SingleThreadMatrix.random(2, 3);
        Matrix m2 = SingleThreadMatrix.random(3, 3);

        assertThatThrownBy(() -> SingleThreadMatrix.multiply(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("These two matrices can not be multiplied. " +
                        "Row count [%d] and column count [%d] are not equal.", 2, 3);
    }

    @Test
    void singleThreadMultiply() {
        Matrix matrix1 = new SingleThreadMatrix(2, 3, 1, 0, 2, -1, 3, 1);
        Matrix matrix2 = new SingleThreadMatrix(3, 2, 3, 1, 2, 1, 1, 0);

        Matrix expected1 = new SingleThreadMatrix(2, 2, 5, 1, 4, 2);
        Matrix result1 = SingleThreadMatrix.multiply(matrix1, matrix2);
        assertThat(result1).isEqualTo(expected1);

        Matrix expected2 = new SingleThreadMatrix(3, 3, 2, 3, 7, 1, 3, 5, 1, 0, 2);
        Matrix result2 = SingleThreadMatrix.multiply(matrix2, matrix1);
        assertThat(result2).isEqualTo(expected2);
    }

    private static @NotNull Stream<Integer> exponentSizes() {
        return MatrixTest.exponentSizes();
    }

    @ParameterizedTest
    @MethodSource("exponentSizes")
    void singleThreadMultiplyWithIncreasingSize(int size) {
        Matrix matrix = SingleThreadMatrix.random(size, size);
        assertThatCode(() -> SingleThreadMatrix.multiply(matrix, matrix)).doesNotThrowAnyException();
    }

}