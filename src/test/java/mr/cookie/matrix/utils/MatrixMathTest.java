package mr.cookie.matrix.utils;

import mr.cookie.matrix.model.Matrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatrixMathTest {

    @Test
    void invalidRowSizesThrowException() {
        Matrix<Integer> m1 = Generator.random(1, 2);
        Matrix<Integer> m2 = Generator.random(2, 2);

        assertThatThrownBy(() -> MatrixMath.add(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same row count. Provided sizes are [%d] and [%d]", 1, 2);
    }

    @Test
    void invalidColumnSizesThrowException() {
        Matrix<Integer> m1 = Generator.random(2, 1);
        Matrix<Integer> m2 = Generator.random(2, 2);

        assertThatThrownBy(() -> MatrixMath.add(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same column count. Provided sizes are [%d] and [%d]", 1, 2);
    }

    @Test
    void add() {
        Matrix<Integer> matrix1 = new Matrix<>(2, 2, 1, 2, 3, 4);
        Matrix<Integer> matrix2 = new Matrix<>(2, 2, 4, 3, 2, 1);
        Matrix<Integer> result = MatrixMath.add(matrix1, matrix2);

        Assertions.assertAll(
                () -> assertThat(result.getRowSize()).isEqualTo(matrix1.getRowSize()),
                () -> assertThat(result.getColumnSize()).isEqualTo(matrix1.getColumnSize()),
                () -> assertThat(result.get(0, 0)).isEqualTo(5),
                () -> assertThat(result.get(1, 1)).isEqualTo(5),
                () -> assertThat(result).isEqualTo(new Matrix<>(2, 2, 5, 5, 5, 5))
        );
    }

}