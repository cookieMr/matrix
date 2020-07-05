package mr.cookie.matrix.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matrix<T extends Number> {

    private final T[] numbers;
    private final int rowSize;
    private final int columnSize;

    @SafeVarargs
    public Matrix(int rowSize, int columnSize, @NotNull T... numbers) {
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

    @NotNull
    public T get(int row, int column) {
        validateRow(row);
        validateColumn(column);

        return numbers[row + rowSize * column];
    }

    @NotNull
    public List<T> getRow(int row) {
        validateRow(row);

        List<T> theRow = new ArrayList<>(rowSize);
        theRow.addAll(Arrays.asList(numbers).subList(row * rowSize, rowSize + row * rowSize));

        return theRow;
    }

    @NotNull
    public List<T> getColumn(int column) {
        validateColumn(column);

        List<T> theColumn = new ArrayList<>(columnSize);
        for (int c = 0; c < columnSize; c++) {
            theColumn.add(numbers[rowSize * c + column]);
        }

        return theColumn;
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
