package mr.cookie.matrix.utils;

import mr.cookie.matrix.model.Matrix;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratorTest {

    private static Stream<Integer> matrixSizes() {
        return Stream.of(1, 10, 100, 1000);
    }

    @ParameterizedTest
    @MethodSource("matrixSizes")
    void randomRowSize(int size) {
        Matrix<Integer> matrix = Generator.random(size, 2);

        assertThat(matrix.getRowSize())
                .isEqualTo(size);
    }

    @ParameterizedTest
    @MethodSource("matrixSizes")
    void randomColumnSize(int size) {
        Matrix<Integer> matrix = Generator.random(2, size);

        assertThat(matrix.getColumnSize())
                .isEqualTo(size);
    }

    @RepeatedTest(3)
    void randomMatrixGeneration() {
        Matrix<Integer> matrix = Generator.random();

        assertThat(matrix.getColumnSize())
                .isGreaterThan(0)
                .isLessThan(1000);
        assertThat(matrix.getRowSize())
                .isGreaterThan(0)
                .isLessThan(1000);
    }

}