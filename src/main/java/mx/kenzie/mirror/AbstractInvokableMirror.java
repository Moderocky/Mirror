package mx.kenzie.mirror;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Executable;

/**
 * A mirror for an invokable type, such as a constructor or a method.
 * This will also cover functional mirrors soon.
 *
 * @param <ReturnType> the return type
 * @param <Subject>    the target object
 */
public abstract class AbstractInvokableMirror<ReturnType, Subject extends Executable>
    extends AbstractAnnotatedMirror<Subject> {
    
    public AbstractInvokableMirror(Subject object) {
        super(object);
    }
    
    public Class<?>[] getParameterTypes() {
        return object.getParameterTypes();
    }
    
    public int getParameterCount() {
        return object.getParameterCount();
    }
    
    public abstract Class<?> getReturnType();
    
    public ReturnType invoke(Object... parameters) {
        throw new IllegalStateException("This has no default implementation - for use by child classes.");
    }
    
    public abstract MethodHandle getHandle();
    
}
