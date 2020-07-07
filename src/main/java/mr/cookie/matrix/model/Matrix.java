package mr.cookie.matrix.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Matrix {

    private final int[] numbers;
    private final int rowSize;
    private final int columnSize;
    private Integer determinant;

    public Matrix(int rowSize, int columnSize, @NotNull int... numbers) {
        if (rowSize < 1) {
            throw new IllegalArgumentException(
                    String.format("Row size must be greater than 0, it was [%d].", rowSize));
        }

        if (columnSize < 1) {
            throw new IllegalArgumentException(
                    String.format("Column size must be greater than 0, it was [%d].", columnSize));
        }

        if (numbers.length != rowSize * columnSize) {
            throw new IllegalArgumentException(
                    String.format("Integers must have length [%d], but the input has length of [%d].",
                            rowSize * columnSize, numbers.length));
        }

        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.numbers = numbers;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    /**
     * Tests if matrix is squared (its row count is the same as column count).
     *
     * @return {@code true} if this matrix is squared, {@code false} otherwise
     */
    public boolean isSquared() {
        return columnSize == rowSize;
    }

    /**
     * Multiplies each element in the matrix by the input constant.
     *
     * @param constant a constant to multiply all elements in the matrix
     */
    public void multiplyByConstant(int constant) {
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] *= constant;
        }
    }

    public int get(int row, int column) {
        validateRow(row);
        validateColumn(column);

        return numbers[row + rowSize * column];
    }

    /**
     * Returns a whole row from this matrix pointed by the provided row index, which is 0th based.
     *
     * @param index a row's 0th based index
     * @return a whole row from this matrix
     */
    @NotNull
    public List<Integer> getRow(int index) {
        validateRow(index);

        List<Integer> theRow = new ArrayList<>(rowSize);
        final int start = index * columnSize;
        for (int i = start; i < start + columnSize; i++) {
            theRow.add(numbers[i]);
        }

        return theRow;
    }

    /**
     * Returns a whole column from this matrix pointed by the provided column index, which is 0th based.
     *
     * @param index a column's 0th based index
     * @return a whole column from this matrix
     */
    @NotNull
    public List<Integer> getColumn(int index) {
        validateColumn(index);

        List<Integer> theColumn = new ArrayList<>(rowSize);
        for (int c = 0; c < rowSize; c++) {
            theColumn.add(numbers[columnSize * c + index]);
        }

        return theColumn;
    }

    /**
     * Returns an integer that is a determinant of this matrix. Calculates this determinant if it wasn't previously
     * calculated, otherwise returns stored value.
     *
     * @return a determinant of this matrix
     */
    //TODO: make it thread safe
    public int getDeterminant() {
        if (!isSquared()) {
            throw new UnsupportedOperationException(String.format(
                    "This matrix is not squared, thus it has no determinant. Its dimensions are [%dx%d].",
                    rowSize, columnSize));
        }

        if (determinant == null) {
            determinant = calculateDeterminant();
        }

        return determinant;
    }

    /**
     * Returns an integer that is a determinant of this matrix.
     *
     * @return a determinant of this matrix
     */
    private int calculateDeterminant() {
        if (rowSize == 1) {
            return numbers[0];
        }

        if (rowSize == 2) {
            return get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1);
        }

        int sum = 0;
        for (int c = 0; c < rowSize; c++) {
            int s = get(0, c) * minorMatrix(0, c).getDeterminant();
            if (c % 2 == 0) {
                sum += s;
            } else {
                sum -= s;
            }
        }

        return sum;
    }

    /**
     * Returns a minor matrix, that is also a squared matrix. It's created from a parent matrix by excluding whole row
     * and whole column, that are specified in input parameters.
     *
     * @param row    a row number form a parent matrix to be excluded
     * @param column a column number from a parent matrix to be excluded
     * @return a squared matrix, that is a minor matrix created by excluded specific row & column from a parent matrix
     * @see <a href="https://en.wikipedia.org/wiki/Laplace_expansion">Laplace Expansion</a>
     */
    @NotNull
    private Matrix minorMatrix(int row, int column) {
        final int minorSize = rowSize - 1;
        int[] minorNumber = new int[minorSize * minorSize];
        int index = 0;

        for (int c = 0; c < rowSize; c++) {
            if (c == column) {
                continue;
            }
            for (int r = 0; r < rowSize; r++) {
                if (r == row) {
                    continue;
                }
                minorNumber[index++] = get(r, c);
            }
        }

        return new Matrix(minorSize, minorSize, minorNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowSize, columnSize, numbers);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof Matrix)) {
            return false;
        }

        Matrix that = (Matrix) o;

        return new EqualsBuilder()
                .append(rowSize, that.rowSize)
                .append(columnSize, that.columnSize)
                .append(numbers, that.numbers)
                .isEquals();
    }

    private void validateRow(int row) {
        if (row < 0 || row >= rowSize) {
            throw new IndexOutOfBoundsException(String.format("Row is [%d] while row count is [%d].", row, rowSize));
        }
    }

    private void validateColumn(int column) {
        if (column < 0 || column >= columnSize) {
            throw new IndexOutOfBoundsException(
                    String.format("Column is [%d] while column count is [%d].", column, columnSize));
        }
    }

}
