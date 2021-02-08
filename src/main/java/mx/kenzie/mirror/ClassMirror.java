package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.Reflected;
import mx.kenzie.mirror.error.CapturedReflectionException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;

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
    
    public <ReturnType> MethodMirror<ReturnType> method(final String name, final Class<?>... parameters) {
        try {
            final Method method = object.getDeclaredMethod(name, parameters);
            if (Modifier.isStatic(method.getModifiers())) return new TargetedMethodMirror<>(method, null);
            return new MethodMirror<>(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
