package mr.cookie.matrix.model;

import mr.cookie.matrix.random.Random;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * An abstract class that represents a matrix.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Matrix_(mathematics)">Wikipedia - Matrix (mathematics)</a>
 */
public abstract class Matrix {

    public static final AtomicReference<ExecutorService> EXECUTOR =
            new AtomicReference<>(Executors.newFixedThreadPool(10));

    public static final AtomicReference<Semaphore> SEMAPHORE =
            new AtomicReference<>(new Semaphore(10, true));

    public static final int MAX_ALLOWED_SIZE = 1000;
    private static final int MAX_ALLOWED_NUMBER = 100;

    private final int[] elements;
    private final int rowSize;
    private final int columnSize;
    protected Integer determinant;

    /**
     * Constructs new matrix with specified size and specified elements. Verifies if row & column sizes are
     * greater than zero and that input elements can fill up whole matrix (their size is exactly
     * {@code rowSize * columnSize}). If not then {@link IllegalArgumentException} with a proper message is thrown.
     *
     * @param rowSize    a number of rows in the new matrix (must be greater than zero)
     * @param columnSize a number of column in the new matrix (must be greater than zero)
     * @param elements   elements of the new matrix, it needs to be the length of {@code rowSize * columnSize}
     */
    public Matrix(int rowSize, int columnSize, @NotNull int... elements) {
        if (rowSize < 1) {
            throw new IllegalArgumentException(
                    String.format("Row size must be greater than 0, it was [%d].", rowSize));
        }

        if (columnSize < 1) {
            throw new IllegalArgumentException(
                    String.format("Column size must be greater than 0, it was [%d].", columnSize));
        }

        if (elements.length != rowSize * columnSize) {
            throw new IllegalArgumentException(
                    String.format("Integers must have length [%d], but the input has length of [%d].",
                            rowSize * columnSize, elements.length));
        }

        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.elements = elements;
    }

    /**
     * Constructs new matrix with specified size and specified elements. Verifies if row & column sizes are
     * greater than zero and that input elements can fill up whole matrix (their size is exactly
     * {@code rowSize * columnSize}). If not then {@link IllegalArgumentException} with a proper message is thrown.
     *
     * @param rowSize    a number of rows in the new matrix (must be greater than zero)
     * @param columnSize a number of column in the new matrix (must be greater than zero)
     * @param elements   list of elements of the new matrix, it needs to be the length of {@code rowSize * columnSize}
     */
    public Matrix(int rowSize, int columnSize, @NotNull List<Integer> elements) {
        this(rowSize, columnSize, elements.stream()
                .mapToInt(Integer::intValue)
                .toArray());
    }

    /**
     * Sets a new executor to be used during matrices multiplication.
     *
     * @param executor a new executor to be used during matrices multiplication
     */
    public static void setExecutor(@NotNull ExecutorService executor) {
        EXECUTOR.set(executor);
    }

    public static void setSemaphorePermits(int permits) {
        if (permits < 1) {
            throw new IllegalArgumentException("Count of permits for semaphore must be a positive number.");
        }
        SEMAPHORE.set(new Semaphore(permits, true));
    }

    /**
     * Returns a new matrix that is a product of addition of two input matrices. Both matrices need to have the same
     * size (row count and column count). Otherwise {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix that will be a part of addition product
     * @param m2 second matrix that will be a part of addition product
     * @return a new matrix that is a product of addition of two matrices
     */
    public static @NotNull Matrix add(@NotNull Matrix m1, @NotNull Matrix m2) {
        verifyRowCount(m1, m2);
        verifyColumnCount(m1, m2);

        final int rowSize = m1.getRowSize();
        final int columnSize = m1.getColumnSize();

        int[] numbers = new int[rowSize * columnSize];
        for (int c = 0; c < columnSize; c++) {
            List<Integer> rowM1 = m1.getRow(c);
            List<Integer> rowM2 = m2.getRow(c);
            for (int r = 0; r < rowSize; r++) {
                numbers[rowSize * c + r] = rowM1.get(r) + rowM2.get(r);
            }
        }

        return new SingleThreadMatrix(rowSize, columnSize, numbers);
    }

    /**
     * Returns a new matrix that is a product of subtraction of two input matrices. Both matrices need to have the same
     * size (row count and column count). Otherwise {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix from which the other matrix will be subtracted
     * @param m2 second matrix which will be subtracted from the other matrix
     * @return a new matrix that is a product of subtraction of two matrices
     */
    public static @NotNull Matrix subtract(@NotNull Matrix m1, @NotNull Matrix m2) {
        verifyRowCount(m1, m2);
        verifyColumnCount(m1, m2);

        final int rowSize = m1.getRowSize();
        final int columnSize = m1.getColumnSize();

        int[] numbers = new int[rowSize * columnSize];
        for (int c = 0; c < columnSize; c++) {
            List<Integer> rowM1 = m1.getRow(c);
            List<Integer> rowM2 = m2.getRow(c);
            for (int r = 0; r < rowSize; r++) {
                numbers[rowSize * c + r] = rowM1.get(r) - rowM2.get(r);
            }
        }

        return new SingleThreadMatrix(rowSize, columnSize, numbers);
    }

    /**
     * Returns a row count, a width of this matrix.
     *
     * @return a row count
     */
    public int getRowSize() {
        return rowSize;
    }

    /**
     * Returns a column count, a height of this matrix.
     *
     * @return a column count
     */
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
        for (int i = 0; i < elements.length; i++) {
            elements[i] *= constant;
        }
    }

    /**
     * Returns a specific element from this matrix, pointed out by coordinated (row & column number)
     * that are oth based indices. Both input row & column numbers must be a valid matrix coordinates.
     * Otherwise {@link IndexOutOfBoundsException} is thrown with a proper message.
     *
     * @param row    0th based row number of the element
     * @param column 0th based column number of the element
     * @return an element from the matrix
     */
    public int get(int row, int column) {
        validateRow(row);
        validateColumn(column);

        return elements[row + rowSize * column];
    }

    /**
     * Returns a whole row from this matrix pointed by the provided row index, which is 0th based. A row index must be
     * a valid one. Otherwise {@link IndexOutOfBoundsException} is thrown with a proper message.
     *
     * @param index a row's 0th based index
     * @return a whole row from this matrix
     */
    public @NotNull List<Integer> getRow(int index) {
        validateRow(index);

        final int start = index * columnSize;

        int[] subArray = ArrayUtils.subarray(elements, start, start + columnSize);
        return Arrays.stream(subArray).boxed().collect(Collectors.toList());
    }

    /**
     * Returns a whole column from this matrix pointed by the provided column index, which is 0th based. A column index
     * must be a valid one. Otherwise {@link IndexOutOfBoundsException} is thrown with a proper message.
     *
     * @param index a column's 0th based index
     * @return a whole column from this matrix
     */
    public @NotNull List<Integer> getColumn(int index) {
        validateColumn(index);

        List<Integer> theColumn = new ArrayList<>(rowSize);
        for (int c = 0; c < rowSize; c++) {
            theColumn.add(elements[columnSize * c + index]);
        }

        return theColumn;
    }

    /**
     * Returns an integer that is a determinant of this matrix. Calculates this determinant if it wasn't previously
     * calculated, otherwise returns stored value. Throws an {@link UnsupportedOperationException} is the matrix is
     * not squared, since only such matrices can have determinants.
     *
     * @return a determinant of this matrix
     */
    public int getDeterminant() {
        if (!isSquared()) {
            throw new UnsupportedOperationException(String.format(
                    "This matrix is not squared, thus it has no determinant. Its dimensions are [%dx%d].",
                    getRowSize(), getColumnSize()));
        }

        if (determinant == null) {
            determinant = calculateDeterminant();
        }

        return determinant;
    }

    /**
     * Returns an integer that is a determinant of this matrix. The assumption is that this matrix is a squared one.
     *
     * @return a determinant of this matrix
     */
    protected int calculateDeterminant() {
        if (getRowSize() == 1) {
            return get(0, 0);
        }

        if (getRowSize() == 2) {
            return get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1);
        }

        int sum = 0;
        for (int c = 0; c < getRowSize(); c++) {
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
     * and whole column, that are specified in input parameters. The assumption is that prent matrix and a new minor
     * matrix are both squared matrices.
     *
     * @param row    a row number form a parent matrix to be excluded
     * @param column a column number from a parent matrix to be excluded
     * @return a squared matrix, that is a minor matrix created by excluded specific row & column from a parent matrix
     * @see <a href="https://en.wikipedia.org/wiki/Laplace_expansion">Laplace Expansion</a>
     */
    protected abstract @NotNull Matrix minorMatrix(int row, int column);

    /**
     * Returns elements for minor matrix which size is less by 1 on each side compared to the parent matrix.
     * Returned elements do not contain values from specified row nor column.
     *
     * @param row    a row number form a parent matrix to be excluded
     * @param column a column number from a parent matrix to be excluded
     * @return elements for minor matrix
     */
    protected @NotNull int[] getMinorMatrixElements(int row, int column) {
        int minorSize = getRowSize() - 1;
        int[] minorNumber = new int[minorSize * minorSize];
        int index = 0;

        for (int c = 0; c < getRowSize(); c++) {
            if (c == column) {
                continue;
            }
            for (int r = 0; r < getRowSize(); r++) {
                if (r == row) {
                    continue;
                }
                minorNumber[index++] = get(r, c);
            }
        }

        return minorNumber;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(rowSize)
                .append(columnSize)
                .append(elements)
                .hashCode();
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
                .append(elements, that.elements)
                .isEquals();
    }

    /**
     * Validates if row number is a valid row index inside this matrix. If not then
     * {@link IndexOutOfBoundsException} with a proper message is thrown.
     *
     * @param row a row number to be validated
     */
    protected void validateRow(int row) {
        if (row < 0 || row >= rowSize) {
            throw new IndexOutOfBoundsException(String.format("Row index is [%d] while row count is [%d].", row, rowSize));
        }
    }

    /**
     * Validates if column number is a valid column index inside this matrix. If not then
     * {@link IndexOutOfBoundsException} with a proper message is thrown.
     *
     * @param column a column number to be validated
     */
    protected void validateColumn(int column) {
        if (column < 0 || column >= columnSize) {
            throw new IndexOutOfBoundsException(
                    String.format("Column index is [%d] while column count is [%d].", column, columnSize));
        }
    }

    /**
     * Returns a primitive integers' array of specified size generated with the use
     * of a repeatable PRNG {@link Random}.
     *
     * @param size count of elements to be generated
     * @return a primitive integers' array of specified size
     */
    protected static int[] getRandomElements(int size) {
        int[] numbers = new int[size];

        int index = 0;
        while (index < size) {
            numbers[index] = MAX_ALLOWED_NUMBER - Random.nextInt(2 * MAX_ALLOWED_NUMBER);
            index++;
        }
        return numbers;
    }

    /**
     * Verifies if first input matrix row count is the same as the second input matrix column count (and vice versa).
     * If not, then {@link IllegalArgumentException} will be thrown.
     *
     * @param m1 first matrix which row & column count will be compared with the other matrix column & row count
     * @param m2 second matrix which column & row count will be compared with the other matrix row & column count
     */
    protected static void verifyRowAndColumnCountsForMultiplication(@NotNull Matrix m1, @NotNull Matrix m2) {
        if (m1.getColumnSize() != m2.getRowSize()) {
            throw new IllegalArgumentException(String.format("These two matrices can not be multiplied. " +
                    "Column count [%d] and row count [%d] are not equal.", m1.getColumnSize(), m2.getRowSize()));
        }

        if (m1.getRowSize() != m2.getColumnSize()) {
            throw new IllegalArgumentException(String.format("These two matrices can not be multiplied. " +
                    "Row count [%d] and column count [%d] are not equal.", m1.getRowSize(), m2.getColumnSize()));
        }
    }

    /**
     * Verifies row counts for both input matrices. Row counts need to have the same value. If not then
     * {@link IllegalArgumentException} will be thrown with a proper message.
     *
     * @param m1 first matrix which row count will be compared with the other matrix
     * @param m2 second matrix which row count will be compared with the other matrix
     */
    protected static void verifyRowCount(@NotNull Matrix m1, @NotNull Matrix m2) {
        if (m1.getRowSize() != m2.getRowSize()) {
            throw new IllegalArgumentException(
                    String.format("Both matrices must have the same row count. Provided sizes are [%d] and [%d]",
                            m1.getRowSize(), m2.getRowSize()));
        }
    }

    /**
     * Verifies column counts for both input matrices. Column counts need to have the same value. If not then
     * {@link IllegalArgumentException} will be thrown with a proper message.
     *
     * @param m1 first matrix which column count will be compared with the other matrix
     * @param m2 second matrix which column count will be compared with the other matrix
     */
    protected static void verifyColumnCount(@NotNull Matrix m1, @NotNull Matrix m2) {
        if (m1.getColumnSize() != m2.getColumnSize()) {
            throw new IllegalArgumentException(
                    String.format("Both matrices must have the same column count. Provided sizes are [%d] and [%d]",
                            m1.getColumnSize(), m2.getColumnSize()));
        }
    }

}
