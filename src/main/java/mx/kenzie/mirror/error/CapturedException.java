package mx.kenzie.mirror.error;

/**
 * Represents a captured exception that can be re-thrown.
 * <p>
 * This is done to mask run-time exceptions that should not be thrown during normal execution.
 */
public class CapturedException extends RuntimeException {
    
    public CapturedException() {
        super();
    }
    
    public CapturedException(String message) {
        super(message);
    }
    
    public CapturedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CapturedException(Throwable cause) {
        super(cause);
    }
    
    protected CapturedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
