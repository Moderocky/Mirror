package mx.kenzie.mirror;

import org.jetbrains.annotations.TestOnly;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Mirrors a type for accessing its members.
 *
 * @param <Thing> the type of the mirrored object
 */
public class Mirror<Thing> {
    
    protected static final LookingGlass GLASS = new LookingGlass();
    
    protected final Thing target;
    protected LookingGlass glass;
    
    protected Mirror(Thing target) {
        this.target = target;
        this.glass = GLASS;
    }
    
    /**
     * Mirror a class for accessing static members.
     *
     * @param type     the class to mirror
     * @param <Target> the class-type
     * @return the mirror
     */
    public static <Target> Mirror<Class<Target>> of(Class<Target> type) {
        return new Mirror<>(type);
    }
    
    /**
     * Creates a 'magic' mirror that follows the methods of the provided class.
     * Using an interface here is recommended, but allows any class type.
     *
     * @param template   the template class to use
     * @param <Template> the template type
     * @return the populated template
     */
    public <Template> Template magic(Class<Template> template) {
        if (Modifier.isFinal(template.getModifiers()))
            throw new IllegalArgumentException("Template must not be final.");
        return glass.makeInlineProxy(Mirror.of(target), template);
    }
    
    /**
     * Creates an intrinsic 'magic' mirror that follows the methods of the provided class.
     * Using an interface here is recommended, but allows any class type.
     * This will invoke the methods on the target object directly rather than using an accessor.
     *
     * @param template   the template class to use
     * @param <Template> the template type
     * @return the populated template
     */
    public <Template> Template magicIntrinsic(Class<Template> template) {
        if (Modifier.isFinal(template.getModifiers()))
            throw new IllegalArgumentException("Template must not be final.");
        return glass.makeIntrinsicProxy(Mirror.of(target), template);
    }
    
    /**
     * Mirror an object for accessing dynamic members.
     *
     * @param thing    the object to mirror
     * @param <Target> the object-type
     * @return the mirror
     */
    public static <Target> Mirror<Target> of(Target thing) {
        return new Mirror<>(thing);
    }
    
    /**
     * Obtain a constructor accessor.
     *
     * @param parameters the constructor parameter types
     * @param <Type>     the type this constructor makes
     * @return the constructor accessor
     */
    public <Type> ConstructorAccessor<Type> constructor(Class<?>... parameters) {
        if (target instanceof Class<?> type)
            return glass.createAccessor(type, glass.findConstructor(type, parameters));
        return glass.createAccessor(target.getClass(), glass.findConstructor(target.getClass(), parameters));
    }
    
    /**
     * Obtain a constructor accessor from a constructor object.
     *
     * @param constructor the constructor
     * @param <Type>      the type this constructor makes
     * @return the constructor accessor
     */
    public <Type> ConstructorAccessor<Type> constructor(Constructor<Type> constructor) {
        return glass.createAccessor(target.getClass(), constructor);
    }
    
    /**
     * Obtain a method accessor.
     *
     * @param name       the method name
     * @param parameters the method parameter types
     * @param <Return>   the return type of this method
     * @return the method accessor
     */
    public <Return> MethodAccessor<Return> method(String name, Class<?>... parameters) {
        if (target instanceof Class<?> type)
            return glass.createAccessor(target, glass.findMethod(type, name, parameters));
        return glass.createAccessor(target, glass.findMethod(target.getClass(), name, parameters));
    }
    
    /**
     * Obtain a method accessor.
     *
     * @param name      the method name
     * @param arguments the method arguments
     * @param <Return>  the return type of this method
     * @return the method accessor
     */
    public <Return> MethodAccessor<Return> method(String name, Object... arguments) {
        if (target instanceof Class<?> type)
            return glass.createAccessor(target, glass.findSmartMethod(type, name, arguments));
        return glass.createAccessor(target, glass.findSmartMethod(target.getClass(), name, arguments));
    }
    
    /**
     * Obtain a method accessor from a method object.
     *
     * @param method   the method
     * @param <Return> the return type of this method
     * @return the method accessor
     */
    public <Return> MethodAccessor<Return> method(Method method) {
        return glass.createAccessor(target, method);
    }
    
    /**
     * Obtain a field accessor.
     *
     * @param name   the field name
     * @param <Type> the type of this field
     * @return the method accessor
     */
    public <Type> FieldAccessor<Type> field(String name) {
        if (target instanceof Class<?> type)
            return glass.createAccessor(target, glass.findField(type, name));
        return glass.createAccessor(target, glass.findField(target.getClass(), name));
    }
    
    /**
     * Obtain a field accessor from a field object.
     *
     * @param field  the field
     * @param <Type> the type of this field
     * @return the method accessor
     */
    public <Type> FieldAccessor<Type> field(Field field) {
        return glass.createAccessor(target, field);
    }
    
    public Mirror<Thing> unsafe() {
        this.glass = new DarkGlass();
        return this;
    }
    
    Field findField(String name) {
        if (target instanceof Class<?> type)
            return glass.findField(type, name);
        return glass.findField(target.getClass(), name);
    }
    
    Method findMethod(String name, Class<?>... parameters) {
        if (target instanceof Class<?> type)
            return glass.findMethod(type, name, parameters);
        return glass.findMethod(target.getClass(), name, parameters);
    }
    
    Class<?> emergentClass() {
        if (target instanceof Class<?> type) return type;
        return target.getClass();
    }
    
    protected Class<?> loadClass(String name, byte[] bytecode) {
        return glass.loadClass(name, bytecode);
    }
    
    protected Class<?> loadClass(Class<?> target, String name, byte[] bytecode) {
        return glass.loadClass(target, name, bytecode);
    }
    
    @TestOnly
    protected byte[] retrieveCode(final Accessor object) {
        if (object instanceof FieldAccessor<?> accessor)
            return glass.writeFieldAccessor(accessor.getTargetType(), accessor.reflect(), accessor.getClass().getName()
                .replace('.', '/'));
        if (object instanceof MethodAccessor<?> accessor)
            return glass.writeMethodAccessor(accessor.getTargetType(), accessor.reflect(), accessor.getClass().getName()
                .replace('.', '/'));
        if (object instanceof ConstructorAccessor<?> accessor)
            return glass.writeConstructorAccessor(accessor.getTargetType(), accessor.reflect(), accessor.getClass()
                .getName().replace('.', '/'));
        return new byte[0];
    }
    
}
