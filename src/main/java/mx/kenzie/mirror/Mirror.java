package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.Reflected;
import mx.kenzie.mirror.error.CapturedReflectionException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mirror wrapping an object.
 *
 * @param <Subject> the target object
 */
public class Mirror<Subject> extends AbstractMirror<Subject> {
    
    public Mirror(Subject object) {
        super(object);
    }
    
    @SuppressWarnings("unchecked")
    public <UnknownType extends Reflected<Subject>> @NotNull
        UnknownType magic(Class<?>... interfaces) {
        final List<Class<?>> list = new ArrayList<>(Arrays.asList(interfaces));
        if (!list.contains(Reflected.class)) list.add(Reflected.class);
        return (UnknownType) Proxy
            .newProxyInstance(InternalAccessor.CLASS_LOADER, list.toArray(new Class[0]), new Invoker<>(object));
    }
    
    public <UnknownType> FastFieldMirror<UnknownType> fastField(final String name) {
        final Field field = Utilities.findField(object.getClass(), name);
        return new TargetedFastFieldMirror<>(field,
            Utilities.getNativeAccessor(field),
            object,
            (Class<UnknownType>) object.getClass());
    }
    
    public <UnknownType> FastFieldMirror<UnknownType> fastField(final String name, final Class<UnknownType> type) {
        final Field field = Utilities.findField(object.getClass(), name, type);
        return new TargetedFastFieldMirror<>(field, Utilities.getNativeAccessor(field), object, type);
    }
    
    public <UnknownType> TargetedFieldMirror<UnknownType> field(final String name) {
        final Field field = Utilities.findField(object.getClass(), name);
        return new TargetedFieldMirror<>(field, object, (Class<UnknownType>) object.getClass());
    }
    
    public <UnknownType> TargetedFieldMirror<UnknownType> field(final String name, final Class<UnknownType> type) {
        final Field field = Utilities.findField(object.getClass(), name, type);
        return new TargetedFieldMirror<>(field, object, (Class<UnknownType>) object.getClass());
    }
    
    public <UnknownType> TargetedFieldMirror<UnknownType> fieldFrom(final String name, final Class<?> owner) {
        assert owner.isAssignableFrom(object.getClass()); // If not, the object doesn't have that field!
        final Field field = Utilities.findField(owner, name);
        return new TargetedFieldMirror<>(field, object);
    }
    
    public <UnknownType> TargetedFieldMirror<UnknownType> fieldFrom(final String name, final Class<?> owner, final Class<UnknownType> type) {
        assert owner.isAssignableFrom(object.getClass()); // If not, the object doesn't have that field!
        final Field field = Utilities.findField(owner, name, type);
        return new TargetedFieldMirror<>(field, object);
    }
    
    public <ReturnType> MethodMirror<ReturnType> methodNamed(final String name) {
        final Method method = Utilities.findMethod(object.getClass(), name);
        return new TargetedMethodMirror<>(method, object);
    }
    
    public <ReturnType> MethodMirror<ReturnType> method(final String name, final Class<?>... parameters) {
        try {
            final Method method = object.getClass().getDeclaredMethod(name, parameters);
            return new TargetedMethodMirror<>(method, object);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    public <ReturnType> FastMethodMirror<ReturnType> fastMethodNamed(final String name) {
        final Method method = Utilities.findMethod(object.getClass(), name);
        return new TargetedFastMethodMirror<>(method, Utilities.getNativeAccessor(method), object);
    }
    
    public <ReturnType> FastMethodMirror<ReturnType> fastMethod(final String name, final Class<?>... parameters) {
        try {
            final Method method = object.getClass().getDeclaredMethod(name, parameters);
            return new TargetedFastMethodMirror<>(method, Utilities.getNativeAccessor(method), object);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public <ReturnType> ReturnType invoke(final String name, final Object... parameters) throws CapturedReflectionException {
        try {
            final Method method = Utilities.getMatchingMethod(type, name, parameters);
            assert method != null;
            Utilities.prepareForAccess(method);
            if (Modifier.isStatic(method.getModifiers()))
                return (ReturnType) method.invoke(null, parameters);
            return (ReturnType) method.invoke(object, parameters);
        } catch (IllegalAccessException | InvocationTargetException | AssertionError e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    /**
     * Creates a new instance of the object parallel to this one.
     * This will succeed by any means - even if it has to allocate a new instance in memory.
     * @param parameters the parameters for construction
     * @return
     */
    @SuppressWarnings("unchecked")
    public Subject newParallelInstance(Object... parameters) {
        return new ClassMirror<>((Class<Subject>) type)
            .newInstance(parameters);
    }
    
    public Subject shallowCopy() {
        final Subject copy = Utilities.allocateInstance((Class<Subject>) object.getClass());
        Utilities.shallowCopy(object, copy);
        return copy;
    }
    
    public Subject deepCopy() {
        final Subject copy = Utilities.allocateInstance((Class<Subject>) object.getClass());
        Utilities.deepCopy(object, copy);
        return copy;
    }
    
}
