package vn.civilpro.exception;

public class LockValidationException extends RuntimeException {
    private Object details;
    public LockValidationException(String message) {
        super(message);
    }
    public LockValidationException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
