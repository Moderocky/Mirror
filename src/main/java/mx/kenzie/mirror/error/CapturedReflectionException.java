package mx.kenzie.mirror.error;

/**
 * For wrapping exceptions during reflection that should not be thrown during regular operation.
 * <p>
 * Note that this does not mean said exceptions cannot occur
 * - but that they shouldn't, subject to this being used properly. :)
 */
public class CapturedReflectionException extends CapturedException {
    
    public CapturedReflectionException() {
        super();
    }
    
    public CapturedReflectionException(String message) {
        super(message);
    }
    
    public CapturedReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CapturedReflectionException(Throwable cause) {
        super(cause);
    }
    
}
