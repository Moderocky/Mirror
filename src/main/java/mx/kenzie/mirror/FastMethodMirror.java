package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.MethodAccessorStub;
import mx.kenzie.mirror.error.CapturedReflectionException;

import java.lang.reflect.Method;

/**
 * A fast method accessor mirror.
 * @param <ReturnType> the return type
 */
public sealed class FastMethodMirror<ReturnType>
    extends MethodMirror<ReturnType>
    permits TargetedFastMethodMirror {
    
    protected final Class<?> type;
    private final MethodAccessorStub stub;
    
    public FastMethodMirror(Method method, MethodAccessorStub object, Class<ReturnType> type) {
        super(method);
        this.stub = object;
        this.type = type;
    }
    
    public ReturnType invoke(Object... parameters) {
        throw new IllegalStateException("This has no default implementation - for use by child classes.");
    }
    
    @SuppressWarnings("unchecked")
    public ReturnType invokeOn(Object target, Object... parameters) throws CapturedReflectionException {
        try {
            return (ReturnType) stub.invoke(target, parameters);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
}
