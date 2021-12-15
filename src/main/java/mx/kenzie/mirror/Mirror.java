package mx.kenzie.mirror;

/**
 * Mirrors a type for accessing its members.
 *
 * @param <Thing> the type of the mirrored object
 */
public class Mirror<Thing> {
    
    protected static final LookingGlass GLASS = new LookingGlass();
    
    protected final Thing target;
    
    protected Mirror(Thing target) {
        this.target = target;
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
            return GLASS.createAccessor(type, GLASS.findConstructor(type, parameters));
        return GLASS.createAccessor(target.getClass(), GLASS.findConstructor(target.getClass(), parameters));
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
            return GLASS.createAccessor(target, GLASS.findMethod(type, name, parameters));
        return GLASS.createAccessor(target, GLASS.findMethod(target.getClass(), name, parameters));
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
            return GLASS.createAccessor(target, GLASS.findField(type, name));
        return GLASS.createAccessor(target, GLASS.findField(target.getClass(), name));
    }
    
}
