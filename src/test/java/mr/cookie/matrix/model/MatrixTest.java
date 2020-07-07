package mr.cookie.matrix.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatrixTest {

    private static final Matrix MATRIX_2_BY_3 = new SingleThreadMatrix(2, 3, 1, 2, 3, 4, 5, 6);

    private static @NotNull Stream<Integer> invalidRowAndColumnSizes() {
        return Stream.of(-100, -1, 0);
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
                .hasMessage("Row is [%d] while row count is [%d].", row, MATRIX_2_BY_3.getRowSize());
    }

    @ParameterizedTest
    @MethodSource("invalidRowColumnIndexes")
    void getByInvalidColumn(int column) {
        assertThatThrownBy(() -> MATRIX_2_BY_3.get(1, column))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("Column is [%d] while column count is [%d].", column, MATRIX_2_BY_3.getColumnSize());
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

    private static Stream<Integer> constants() {
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

}