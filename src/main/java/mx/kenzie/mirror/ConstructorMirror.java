package mx.kenzie.mirror;

import mx.kenzie.mirror.error.CapturedReflectionException;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorMirror<ReturnType>
    extends AbstractInvokableMirror<ReturnType, Constructor<ReturnType>> {
    
    protected final Class<?> type;
    
    public ConstructorMirror(Constructor<ReturnType> object) {
        this(object, object.getDeclaringClass());
    }
    
    public ConstructorMirror(Constructor<ReturnType> object, Class<ReturnType> type) {
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
        try {
            return object.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public MethodHandle getHandle() {
        return Utilities.getConstructorHandle(object.getDeclaringClass(), object.getParameterTypes());
    }
}
