package mx.kenzie.mirror;

import mx.kenzie.mirror.error.CapturedReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public sealed class MethodMirror<ReturnType> extends AbstractInvokableMirror<ReturnType, Method> permits FastMethodMirror, TargetedMethodMirror {
    
    protected final Class<?> type;
    
    protected MethodMirror(Method object) {
        this(object, (Class<ReturnType>) object.getReturnType());
    }
    
    protected MethodMirror(Method object, Class<ReturnType> type) {
        super(object);
        Utilities.prepareForAccess(object);
        this.type = type;
    }
    
    public Class<?>[] getParameterTypes() {
        return object.getParameterTypes();
    }
    
    public int getParameterCount() {
        return object.getParameterCount();
    }
    
    public Class<?> getReturnType() {
        return type;
    }
    
    public ReturnType invoke(Object... parameters) {
        throw new IllegalStateException("This has no default implementation - for use by child classes.");
    }
    
    public String getName() {
        return object.getName();
    }
    
    @SuppressWarnings("unchecked")
    public ReturnType invokeOn(Object target, Object... parameters) throws CapturedReflectionException {
        try {
            return (ReturnType) object.invoke(target, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
}
