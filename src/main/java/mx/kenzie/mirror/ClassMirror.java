package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.Reflected;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassMirror<Subject> extends Mirror<Class<Subject>> {
    
    ClassMirror(Class<Subject> object) {
        super(object);
    }
    
    public Subject newInstance(Object... parameters) {
        return Utilities.createInstance(object, parameters);
    }
    
    @Override
    public <UnknownType extends Reflected<Class<Subject>>> @NotNull UnknownType magic(final Class<?>... interfaces) {
        throw new IllegalStateException("Cannot create magic invocation of class.");
    }
    
    @Override
    public <ReturnType> MethodMirror<ReturnType> method(final String name, final Class<?>... parameters) {
        try {
            final Method method = object.getDeclaredMethod(name, parameters);
            if (Modifier.isStatic(method.getModifiers())) return new TargetedMethodMirror<>(method, null);
            return new MethodMirror<>(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    @Override
    public VarHandle getFieldHandle(Class<?> type, String name) {
        return Utilities.getVarHandle(name, type, object, false);
    }
    
    @Override
    public MethodHandle getMethodHandle(Class<?> returnType, String name, Class<?>... parameterTypes) {
        return Utilities.getMethodHandle(name, returnType, effectiveClass(), false, parameterTypes);
    }
    
    @Override
    protected Class<?> effectiveClass() {
        return object;
    }
    
}
