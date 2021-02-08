package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.FieldAccessor;
import mx.kenzie.mirror.copy.FieldAccessorStub;
import mx.kenzie.mirror.copy.MethodAccessorStub;
import mx.kenzie.mirror.copy.ReflectionFactory;
import mx.kenzie.mirror.error.CapturedReflectionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.constant.Constable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class Utilities {
    
    static FieldAccessor getMirroredAccessor(final Object accessor) {
        return new Mirror<>(accessor).magic(FieldAccessor.class);
    }
    
    static Object invoke(@NotNull Method method, @NotNull Object accessor, @Nullable Object target, Object... parameters) throws Throwable {
        return invoke0(accessor, target, parameters);
    }
    
    private static Object invoke0(@NotNull Object accessor, @Nullable Object target, Object... parameters)
        throws InvocationTargetException, IllegalAccessException {
        return InternalAccessor.invoke.invoke(accessor, target, parameters);
    }
    
    static Object invokeNative(@NotNull Method method, @NotNull Object accessor, @Nullable Object target, Object... parameters) throws Throwable {
        return invokeNative0(method, accessor, target, parameters);
    }
    
    private static Object invokeNative0(@NotNull Method method, @NotNull Object accessor, @Nullable Object target, Object... parameters)
        throws InvocationTargetException, IllegalAccessException {
        return InternalAccessor.invoke0.invoke(accessor, method, target, parameters);
    }
    
    static <
        Member extends AccessibleObject & AnnotatedElement & java.lang.reflect.Member
        > void prepareForAccess(final Member object) {
        final Class<?> cls = object.getDeclaringClass();
        try {
            if (object instanceof Method method) {
                addExports(MethodMirror.class, cls, true);
                addOpens(MethodMirror.class, cls, true);
            } else if (object instanceof Field field) {
                addExports(FieldMirror.class, cls, true);
                addOpens(FieldMirror.class, cls, true);
                if (Modifier.isStatic(object.getModifiers()) && Modifier.isFinal(object.getModifiers())) {
                    allowConstantAccess0(field);
                }
            } else {
                addExports(Mirror.class, cls, true);
                addOpens(Mirror.class, cls, true);
            }
            setAccessible(object, true);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    static void addExports(final Class<?> accessor, final Class<?> target, final boolean accessNamedModule) {
        if (!accessNamedModule) addExports(accessor, target);
        else {
            try {
                InternalAccessor.implAddExportsOrOpens.invoke(target.getModule(),
                    target.getPackageName(),
                    accessor.getModule(),
                    false,
                    true);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    static void addOpens(final Class<?> accessor, final Class<?> target, final boolean accessNamedModule) {
        if (!accessNamedModule) addOpens(accessor, target);
        else {
            try {
                InternalAccessor.implAddExportsOrOpens.invoke(target.getModule(),
                    target.getPackageName(),
                    accessor.getModule(),
                    true,
                    true);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void allowConstantAccess0(Field field) throws Throwable {
        InternalAccessor.modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }
    
    static void addExports(final Class<?> accessor, final Class<?> target) {
        target.getModule()
            .addExports(target.getPackageName(), accessor.getModule());
    }
    
    static void addOpens(final Class<?> accessor, final Class<?> target) {
        target.getModule()
            .addOpens(target.getPackageName(), accessor.getModule());
    }
    
    static void setAccessible(final AccessibleObject object, boolean value)
        throws InvocationTargetException, IllegalAccessException {
        InternalAccessor.setAccessible0.invoke(object, value);
    }
    
    static Field getDeclaredField(Class<?> cls, String name) {
        for (final Field field : getDeclaredFields(cls)) {
            if (field.getName().equals(name)) return field;
        }
        return null;
    }
    
    static Field[] getDeclaredFields(Class<?> cls) {
        try {
            return (Field[]) InternalAccessor.getDeclaredFields0.invoke(cls, false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    static <Type> Type createInstance(Class<Type> type, Object... parameters) {
        if (type.isArray()) {
            if (parameters.length > 0 && (canCast0(parameters[0], int.class) || canCast0(parameters[0], Integer.class)))
                return (Type) Array.newInstance(type.getComponentType(), (int) parameters[0]);
            else return (Type) Array.newInstance(type.getComponentType(), 0);
        }
        if (parameters.length == 0 && Utilities.hasNullaryConstructor(type)) {
            try {
                final Constructor<Type> constructor = type.getDeclaredConstructor();
                setAccessible(constructor, true);
                return constructor.newInstance();
            } catch (Throwable ex) {
                throw new CapturedReflectionException(ex);
            }
        } else if (parameters.length == 0) {
            return allocateInstance(type);
        }
        final Constructor<?> constructor = Utilities.getMatchingConstructor(type, parameters);
        if (constructor != null) {
            try {
                InternalAccessor.setAccessible0.invoke(constructor, true);
                return (Type) constructor.newInstance(parameters);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new CapturedReflectionException(e);
            }
        }
        if (parameters[0] instanceof Constructor<?> replacement) {
            if (parameters.length == 1) {
                return allocateInstance(type, replacement);
            } else {
                final Object[] remainingArgs = Arrays.copyOfRange(parameters, 1, parameters.length);
                return allocateInstance(type, replacement, remainingArgs);
            }
        }
        throw new IllegalArgumentException("Unable to find a constructor for " + type
            .getName() + " that accepts " + Arrays
            .toString(parameters));
    }
    
    private static boolean canCast0(Object object, Class<?> type) {
        try {
            type.cast(object);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
    
    static boolean hasNullaryConstructor(final Class<?> cls) {
        try {
            return cls.getDeclaredConstructor() != null;
        } catch (Throwable ex) {
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    static <Type> Type allocateInstance(Class<Type> type) {
        try {
            return (Type) InternalAccessor.externalUnsafe.allocateInstance(type);
        } catch (Throwable ex) {
            throw new CapturedReflectionException(ex);
        }
    }
    
    static <Type> Constructor<Type> getMatchingConstructor(final Class<?> cls, final Object... parameters) {
        for (final Constructor<?> constructor : cls.getDeclaredConstructors())
            check:{
                if (constructor.getParameterCount() != parameters.length) continue;
                final Class<?>[] classes = constructor.getParameterTypes();
                for (int i = 0; i < parameters.length; i++) {
                    if (!classes[i].isAssignableFrom(parameters[i].getClass())) break check;
                }
                return (Constructor<Type>) constructor;
            }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    static <Type> Type allocateInstance(Class<Type> type,
                                        Constructor<?> constructor,
                                        Object... remainingArgs) {
        try {
            Constructor<Type> indolent = (Constructor<Type>) InternalAccessor.externalFactory
                .newConstructorForSerialization(type, constructor);
            indolent.setAccessible(true);
            return indolent.newInstance(remainingArgs);
        } catch (Throwable ex) {
            throw new CapturedReflectionException(ex);
        }
    }
    
    static Method getMatchingMethod(final Class<?> cls, final String name, final Object... parameters) {
        for (final Method method : cls.getDeclaredMethods())
            check:{
                if (!method.getName().equals(name)) break check;
                if (method.getParameterCount() != parameters.length) continue;
                final Class<?>[] classes = method.getParameterTypes();
                for (int i = 0; i < parameters.length; i++) {
                    if (!classes[i].isAssignableFrom(parameters[i].getClass())) break check;
                }
                return method;
            }
        return null;
    }
    
    static void scrape(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) continue;
            StringBuilder builder = new StringBuilder()
                .append("    public native ")
                .append(method.getReturnType().getSimpleName())
                .append(" ")
                .append(method.getName())
                .append("(");
            if (method.getParameterCount() > 0) {
                final Class<?>[] types = method.getParameterTypes();
                for (int i = 0; i < types.length; i++) {
                    builder.append(types[i].getSimpleName())
                        .append(" $")
                        .append(i);
                    if (i < types.length - 1) builder.append(", ");
                }
            }
            builder.append(");");
            System.out.println(builder);
        }
    }
    
    static Method findMethod(Class<?> owner, String name) {
        for (Method method : owner.getDeclaredMethods()) {
            if (method.getName().equals(name)) return method;
        }
        return null;
    }
    
    static Method findMethod(Class<?> owner, String name, Class<?> returnType) {
        for (Method method : owner.getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getReturnType().equals(returnType)) return method;
        }
        return null;
    }
    
    static Field findField(Class<?> owner, String name) {
        for (Field field : owner.getDeclaredFields()) {
            if (field.getName().equals(name)) return field;
        }
        if (owner.getSuperclass() != null) {
            return findField(owner.getSuperclass(), name);
        }
        return null;
    }
    
    static Field findField(Class<?> owner, String name, Class<?> type) {
        for (Field field : owner.getDeclaredFields()) {
            if (field.getName().equals(name) && field.getType().equals(type)) return field;
        }
        if (owner.getSuperclass() != null) {
            return findField(owner.getSuperclass(), name, type);
        }
        return null;
    }
    
    static Class<?> forName(final String path) {
        try {
            return Class.forName(path);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    static void deepCopy(Object source, Object destination) {
        addExports(Utilities.class, destination.getClass());
        addOpens(Utilities.class, destination.getClass());
        shallowCopy(source, destination);
        for (final Field field : destination.getClass().getDeclaredFields()) {
            try {
                if (field.getType().isPrimitive()) continue;
                if (!field.isAccessible()) field.setAccessible(true);
                final Object a = field.get(source);
                if (a instanceof Constable) continue;
                if (a == null) continue;
                final Object b = allocateInstance(a.getClass());
                deepCopy(a, b);
                field.set(destination, b);
            } catch (IllegalAccessException e) {
                // Ignore - our shallow copy is probably fine.
            }
        }
    }
    
    static void shallowCopy(Object source, Object destination) {
        final long sourcePointer, destinationPointer;
        final long length = getMemorySize0(source);
        sourcePointer = getAddress(source);
        destinationPointer = getAddress(destination);
        InternalAccessor.externalUnsafe.copyMemory(sourcePointer, destinationPointer, length);
    }
    
    private static long getMemorySize0(final Object object) {
        final Set<Field> fields = new HashSet<>();
        Class<?> cls = object.getClass();
        while (cls != Object.class) {
            for (final Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                fields.add(field);
            }
            cls = cls.getSuperclass();
        }
        long maxSize = 0;
        for (final Field field : fields) {
            final long offset = InternalAccessor.externalUnsafe.objectFieldOffset(field);
            if (offset > maxSize) maxSize = offset;
        }
        return ((maxSize / 8) + 1) * 8;
    }
    
    static MethodAccessorStub makeBytecodeAccessor(final Method method) {
        final Object object = Utilities.makeFastAccessor(method);
        final Class<?> original = object.getClass();
        final MethodAccessorStub accessor = Utilities.transform0(object, MethodAccessorStub.class);
        Utilities.transform0(object, original);
        return accessor;
    }
    
    static Object makeFastAccessor(final Method method) {
        try {
            return makeFastAccessor0(method);
        } catch (InstantiationException
            | InvocationTargetException
            | NoSuchMethodException
            | IllegalAccessException ex) {
            throw new CapturedReflectionException("Unable to create accessor for '" + method.getName() + "'", ex);
        }
    }
    
    @Deprecated
    private static <T> T transform0(Object object, Class<? super T> cls) {
        if (object == null) return null;
        final T template = (T) allocateInstance(cls);
        final long offset = 8;
        final long[] addresses = new long[]{
            getAddress(template),
            getAddress(object)
        };
        final int klass = InternalAccessor.externalUnsafe.getInt(addresses[0] + offset);
        InternalAccessor.externalUnsafe.putInt(addresses[1] + offset, klass);
        return (T) object;
    }
    
    private static Object makeFastAccessor0(final Method method)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        if (isVMAnonymousClass(method.getDeclaringClass())) {
            return getExistingAccessor(method);
        }
        final Constructor<?> constructor = InternalAccessor.methodAccessorGeneratorClass.getDeclaredConstructor();
        setAccessible(constructor, true);
        final Object generator = constructor.newInstance();
        return InternalAccessor.generateMethod.invoke(generator,
            method.getDeclaringClass(),
            method.getName(),
            method.getParameterTypes(),
            method.getReturnType(),
            method.getExceptionTypes(),
            method.getModifiers(),
            false,
            false,
            null);
    }
    
    @Deprecated
    static ReflectionFactory convertFactory(final Object object) {
        final Class<?> original = object.getClass();
        final ReflectionFactory factory = Utilities.transform0(object, ReflectionFactory.class);
        Utilities.transform0(object, original);
        return factory;
    }
    
    static long getAddress(Object object) {
        final Object[] objects = new Object[]{object};
        final int offset = InternalAccessor.externalUnsafe.arrayBaseOffset(objects.getClass());
        final int scale = InternalAccessor.externalUnsafe.arrayIndexScale(objects.getClass());
        return switch (scale) {
            case 4 -> (InternalAccessor.externalUnsafe.getInt(objects, offset) & 0xFFFFFFFFL) * 8;
//            case 8 -> // TODO: 09/11/2020 Add impl for 8-scaled arrays?
            default -> throw new IllegalStateException("Unknown converter: " + scale);
        };
    }
    
    static boolean isVMAnonymousClass(Class<?> cls) {
        return cls.getName().indexOf('/') > -1;
    }
    
    static Object getExistingAccessor(final Method method) {
        try {
            return getExistingAccessor0(method);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new CapturedReflectionException("Unable to get accessor for '" + method.getName() + "'", ex);
        }
    }
    
    private static Object getExistingAccessor0(final Method method)
        throws InvocationTargetException, IllegalAccessException {
        return InternalAccessor.acquireMethodAccessor.invoke(method);
        // Probably best not to return the child directly since this can ruin JIT optimisation
//        if (InternalAccessor.nativeMethodAccessorImplClass.isInstance(accessor)) return accessor;
//        else if (InternalAccessor.delegatingMethodAccessorImplClass.isInstance(accessor)) {
//            return InternalAccessor.delegate.get(accessor);
//        }
//        return accessor; // Unknown type?? maybe Magic
    }
    
    static MethodAccessorStub getNativeAccessor(final Method method) {
        final Object object = Utilities.getExistingAccessor(method);
        final Class<?> original = object.getClass();
        final MethodAccessorStub accessor = Utilities.transform0(object, MethodAccessorStub.class);
        Utilities.transform0(object, original);
        return accessor;
    }
    
    static FieldAccessorStub getNativeAccessor(final Field field) {
        final Object object = Utilities.getExistingAccessor(field);
        final Class<?> original = object.getClass();
        final FieldAccessorStub accessor = Utilities.transform0(object, FieldAccessorStub.class);
        Utilities.transform0(object, original);
        return accessor;
    }
    
    static Object getExistingAccessor(final Field field) {
        try {
            return getExistingAccessor0(field);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new CapturedReflectionException("Unable to get accessor for '" + field.getName() + "'", ex);
        }
    }
    
    private static Object getExistingAccessor0(final Field field)
        throws InvocationTargetException, IllegalAccessException {
        return InternalAccessor.acquireFieldAccessor.invoke(field, true);
    }
    
    static <Owner> Constructor<Owner> createConstructor(Class<Owner> type, Class<?>... parameterTypes) {
        final Object generator = ClassMirror.ofClass(internal("MethodAccessorGenerator"))
            .newInstance();
        try {
            final Constructor<Constructor> creator = Constructor.class.getDeclaredConstructor(Class.class,
                Class[].class,
                Class[].class,
            int.class,
            int.class,
            String.class,
            byte[].class,
            byte[].class);
            final Constructor<Owner> constructor = ClassMirror.ofConstructor(creator).invoke(
                type, parameterTypes, new Class[0], 0, -1, "", new byte[0], new byte[0]);
            final Object accessor = Mirror.of(generator)
                .method("generateConstructor", Class.class, Class[].class, Class[].class, int.class)
                .invoke(type, parameterTypes, new Class[0], 0);
            new Mirror<>(constructor).invoke("setConstructorAccessor", accessor);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    private static Class<?> internal(String name) {
        return forName("jdk.internal.reflect." + name);
    }
    
}
