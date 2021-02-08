package mx.kenzie.mirror;

import jdk.jshell.execution.Util;
import mx.kenzie.mirror.copy.Reflected;
import mx.kenzie.mirror.error.CapturedReflectionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The base Mirror class that all mirrors extend.
 * <p>
 * Stores a subject and a reference to the subject's class.
 *
 * @param <Subject>
 */
public abstract class AbstractMirror<Subject>
    implements Reflected<Subject> {
    
    protected final @NotNull Subject object;
    protected final @NotNull Class<?> type;
    
    public AbstractMirror(Subject object) {
        this.object = object;
        this.type = object.getClass();
    }
    
    public static <Target> Mirror<Target> of(Target target) {
        return new Mirror<>(target);
    }
    
    public static <Target> Mirror<Target> of(Target target, Class<Target> type) {
        return new Mirror<>(target);
    }
    
    public static <UnknownType> ClassMirror<UnknownType> ofClass(Class<UnknownType> cls) {
        return new ClassMirror<>(cls);
    }
    
    public static <UnknownType>
    @Nullable ClassMirror<UnknownType> ofClass(String path) {
        try {
            return (ClassMirror<UnknownType>) new ClassMirror<>(Class.forName(path));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    public static <UnknownType> ConstructorMirror<UnknownType> ofConstructor(Constructor<UnknownType> constructor) {
        return new ConstructorMirror<>(constructor, constructor.getDeclaringClass());
    }
    
    public static <Owner> ConstructorMirror<Owner> ofConstructor(Class<Owner> type, Class<?>... parameterTypes) {
        try {
            final Constructor<Owner> constructor = type.getDeclaredConstructor(parameterTypes);
            return new ConstructorMirror<>(constructor, type);
        } catch (Throwable ex) {
            throw new CapturedReflectionException(ex);
        }
    }
    
    /**
     * This attempts to create a new constructor for the given parameter types, if the existing one cannot be used.
     * This may be useful in cases of root or tree corruption or some sort of unusual security error.
     *
     * Note that this should generally be avoided - it cannot create a constructor that has no representation
     * in the class bytecode, so will rarely have a purpose.
     */
    public static <Owner> ConstructorMirror<Owner> ofQuasiConstructor(Class<Owner> type, Class<?>... parameterTypes) {
        try {
            final Constructor<Owner> constructor = type.getDeclaredConstructor(parameterTypes);
            return new ConstructorMirror<>(constructor, type);
        } catch (Throwable ex) {
            return new ConstructorMirror<>(Utilities.createConstructor(type, parameterTypes), type);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <UnknownType> MethodMirror<UnknownType> ofMethod(Method method) {
        return (MethodMirror<UnknownType>) new MethodMirror<>(method, method.getReturnType());
    }
    
    @SuppressWarnings("unchecked")
    public static <UnknownType> FieldMirror<UnknownType> ofField(Field field) {
        return (FieldMirror<UnknownType>) new FieldMirror<>(field, field.getType());
    }
    
    @Override
    public @NotNull Subject getOriginal() {
        return object;
    }
    
    @Override
    public @NotNull Class<?> getOriginalClass() {
        return type;
    }
    
    public Class<?> getClass(String path) {
        return Utilities.forName(path);
    }
    
}
