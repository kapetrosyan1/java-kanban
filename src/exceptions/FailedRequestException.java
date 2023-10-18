package exceptions;

public class FailedRequestException extends RuntimeException {
    public FailedRequestException(String m) {
        super(m);
    }
}
