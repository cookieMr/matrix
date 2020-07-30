package mr.cookie.matrix.exceptions;

public class MatrixInterruptedException extends RuntimeException {

    public MatrixInterruptedException(Exception exception) {
        super(exception);
    }

}
