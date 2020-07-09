package mr.cookie.matrix.model;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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
                .isGreaterThan(0)
                .isLessThanOrEqualTo(1000);
        assertThat(matrix.getRowSize())
                .isGreaterThan(0)
                .isLessThanOrEqualTo(1000);
    }

    @Test
    void nonSquaredMatrixThrowsExceptionWhenGettingDeterminant() {
        assertThatThrownBy(() -> SingleThreadMatrix.random(2, 3).getDeterminant())
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

}