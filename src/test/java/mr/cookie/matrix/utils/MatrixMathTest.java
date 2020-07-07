package mr.cookie.matrix.utils;

import mr.cookie.matrix.model.Matrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatrixMathTest {

    @Test
    void invalidRowSizesThrowException() {
        Matrix m1 = Generator.random(1, 2);
        Matrix m2 = Generator.random(2, 2);

        assertThatThrownBy(() -> MatrixMath.add(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same row count. Provided sizes are [%d] and [%d]", 1, 2);
        assertThatThrownBy(() -> MatrixMath.subtract(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same row count. Provided sizes are [%d] and [%d]", 1, 2);
    }

    @Test
    void invalidColumnSizesThrowException() {
        Matrix m1 = Generator.random(2, 1);
        Matrix m2 = Generator.random(2, 2);

        assertThatThrownBy(() -> MatrixMath.add(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same column count. Provided sizes are [%d] and [%d]", 1, 2);
        assertThatThrownBy(() -> MatrixMath.subtract(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both matrices must have the same column count. Provided sizes are [%d] and [%d]", 1, 2);
    }

    @Test
    void firstConditionOfRowAndColumnCheckThrowsException() {
        Matrix m1 = Generator.random(2, 3);
        Matrix m2 = Generator.random(2, 3);

        assertThatThrownBy(() -> MatrixMath.multiply(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("These two matrices can not be multiplied. " +
                        "Column count [%d] and row count [%d] are not equal.", 3, 2);
    }

    @Test
    void secondConditionOfRowAndColumnCheckThrowsException() {
        Matrix m1 = Generator.random(2, 3);
        Matrix m2 = Generator.random(3, 3);

        assertThatThrownBy(() -> MatrixMath.multiply(m1, m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("These two matrices can not be multiplied. " +
                        "Row count [%d] and column count [%d] are not equal.", 2, 3);
    }

    @Test
    void add() {
        Matrix matrix1 = new Matrix(2, 2, 1, 2, 3, 4);
        Matrix matrix2 = new Matrix(2, 2, 4, 3, 2, 1);
        Matrix result = MatrixMath.add(matrix1, matrix2);

        Assertions.assertAll(
                () -> assertThat(result.getRowSize()).isEqualTo(matrix1.getRowSize()),
                () -> assertThat(result.getColumnSize()).isEqualTo(matrix1.getColumnSize()),
                () -> assertThat(result.get(0, 0)).isEqualTo(5),
                () -> assertThat(result.get(1, 1)).isEqualTo(5),
                () -> assertThat(result).isEqualTo(new Matrix(2, 2, 5, 5, 5, 5))
        );
    }

    @Test
    void subtract() {
        Matrix matrix1 = new Matrix(2, 2, 1, 2, 3, 4);
        Matrix matrix2 = new Matrix(2, 2, 4, 3, 2, 1);
        Matrix result = MatrixMath.subtract(matrix1, matrix2);

        Assertions.assertAll(
                () -> assertThat(result.getRowSize()).isEqualTo(matrix1.getRowSize()),
                () -> assertThat(result.getColumnSize()).isEqualTo(matrix1.getColumnSize()),
                () -> assertThat(result.get(0, 0)).isEqualTo(-3),
                () -> assertThat(result.get(1, 1)).isEqualTo(3),
                () -> assertThat(result).isEqualTo(new Matrix(2, 2, -3, -1, 1, 3))
        );
    }

    @Test
    void multiply() {
        Matrix matrix1 = new Matrix(2, 3, 1, 0, 2, -1, 3, 1);
        Matrix matrix2 = new Matrix(3, 2, 3, 1, 2, 1, 1, 0);

        Matrix expected1 = new Matrix(2, 2, 5, 1, 4, 2);
        Matrix result1 = MatrixMath.multiply(matrix1, matrix2);
        assertThat(result1).isEqualTo(expected1);

        Matrix expected2 = new Matrix(3, 3, 2, 3, 7, 1, 3, 5, 1, 0, 2);
        Matrix result2 = MatrixMath.multiply(matrix2, matrix1);
        assertThat(result2).isEqualTo(expected2);
    }

}