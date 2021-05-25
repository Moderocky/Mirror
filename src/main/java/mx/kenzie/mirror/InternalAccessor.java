package mx.kenzie.mirror;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class InternalAccessor {
    
    static final ClassLoader CLASS_LOADER = InternalAccessor.class.getClassLoader();
    
    static Unsafe externalUnsafe;
    static Object internalUnsafe;
    static ReflectionFactory externalFactory;
    static mx.kenzie.mirror.copy.ReflectionFactory internalFactory;
    
    static Class<?> reflectAccessClass;
    static Class<?> methodAccessorClass;
    static Class<?> fieldAccessorClass;
    static Class<?> unsafeFieldAccessorFactoryClass;
    static Class<?> methodAccessorGeneratorClass;
    static Class<?> nativeMethodAccessorImplClass;
    static Class<?> delegatingMethodAccessorImplClass;
    static Class<?> methodAccessorImplClass;
    static Class<?> reflectUtilClass;
    static Class<?> classFileAssemblerClass;
    
    static Method implAddExportsOrOpens;
    static Method getDeclaredConstructors0;
    static Method generateMethod;
    static Method getMethodAccessor;
    static Method acquireMethodAccessor;
    static Method getFieldAccessor;
    static Method acquireFieldAccessor;
    static Method invoke0;
    static Method invoke;
    static Method setAccessible0;
    static Method getDeclaredFields0;
    
    static Field delegate;
    static Field method;
    static Field modifiers;
    
    static {
        try {
            try {
                externalUnsafe = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
                    final Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    return (Unsafe) f.get(null);
                });
            } catch (PrivilegedActionException ex) {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                externalUnsafe = (Unsafe) field.get(null);
            }
            try {
                Field field = Unsafe.class.getDeclaredField("theInternalUnsafe");
                field.setAccessible(true);
                internalUnsafe = field.get(null);
            } catch (Throwable ignore) {
                // null if inaccessible
            }
            final Field moduleField = Class.class.getDeclaredField("module");
            final long offset = externalUnsafe.objectFieldOffset(moduleField);
            externalUnsafe.putObject(InternalAccessor.class, offset, Object.class.getModule());
            externalUnsafe.putObject(Utilities.class, offset, Object.class.getModule());
            externalFactory = ReflectionFactory.getReflectionFactory();
            reflectAccessClass = Class.forName("java.lang.reflect.ReflectAccess");
            methodAccessorClass = Class.forName("jdk.internal.reflect.MethodAccessor");
            fieldAccessorClass = Class.forName("jdk.internal.reflect.FieldAccessor");
            methodAccessorGeneratorClass = Class.forName("jdk.internal.reflect.MethodAccessorGenerator");
            nativeMethodAccessorImplClass = Class.forName("jdk.internal.reflect.NativeMethodAccessorImpl");
            delegatingMethodAccessorImplClass = Class.forName("jdk.internal.reflect.DelegatingMethodAccessorImpl");
            methodAccessorImplClass = Class.forName("jdk.internal.reflect.MethodAccessorImpl");
            reflectUtilClass = Class.forName("sun.reflect.misc.ReflectUtil");
            unsafeFieldAccessorFactoryClass = Class.forName("jdk.internal.reflect.UnsafeFieldAccessorFactory");
            classFileAssemblerClass = Class.forName("jdk.internal.reflect.ClassFileAssembler");
            implAddExportsOrOpens = Module.class.getDeclaredMethod("implAddExportsOrOpens",
                String.class, Module.class, boolean.class, boolean.class);
            implAddExportsOrOpens.setAccessible(true);
            breakEncapsulation(Class.class);
            breakEncapsulation(Method.class);
            breakEncapsulation(Field.class);
            breakEncapsulation(AccessibleObject.class);
            breakEncapsulation(nativeMethodAccessorImplClass);
            breakEncapsulation(delegatingMethodAccessorImplClass);
            breakEncapsulation(methodAccessorImplClass);
            breakEncapsulation(methodAccessorGeneratorClass);
            breakEncapsulation(classFileAssemblerClass);
            getDeclaredConstructors0 = Class.class.getDeclaredMethod("getDeclaredConstructors0", boolean.class);
            getDeclaredConstructors0.setAccessible(true);
            setAccessible0 = AccessibleObject.class.getDeclaredMethod("setAccessible0", boolean.class);
            setAccessible0.setAccessible(true);
            invoke0 = nativeMethodAccessorImplClass
                .getDeclaredMethod("invoke0", Method.class, Object.class, Object[].class);
            setAccessible0.invoke(invoke0, true);
            delegate = delegatingMethodAccessorImplClass.getDeclaredField("delegate");
            setAccessible0.invoke(delegate, true);
            method = nativeMethodAccessorImplClass.getDeclaredField("method");
            setAccessible0.invoke(method, true);
            getMethodAccessor = Method.class.getDeclaredMethod("getMethodAccessor");
            setAccessible0.invoke(getMethodAccessor, true);
            acquireMethodAccessor = Method.class.getDeclaredMethod("acquireMethodAccessor");
            setAccessible0.invoke(acquireMethodAccessor, true);
            invoke = methodAccessorClass.getDeclaredMethod("invoke", Object.class, Object[].class);
            setAccessible0.invoke(invoke, true);
            generateMethod = methodAccessorGeneratorClass.getDeclaredMethod("generate",
                Class.class,
                String.class,
                Class[].class,
                Class.class,
                Class[].class,
                int.class,
                boolean.class,
                boolean.class,
                Class.class);
            setAccessible0.invoke(generateMethod, true);
            getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            setAccessible0.invoke(getDeclaredFields0, true);
            modifiers = Utilities.getDeclaredField(Field.class, "modifiers");
            setAccessible0.invoke(modifiers, true);
            getFieldAccessor = Field.class.getDeclaredMethod("getFieldAccessor", boolean.class);
            setAccessible0.invoke(getFieldAccessor, true);
            acquireFieldAccessor = Field.class.getDeclaredMethod("acquireFieldAccessor", boolean.class);
            setAccessible0.invoke(acquireFieldAccessor, true);
            try {
                Field field = ReflectionFactory.class.getDeclaredField("delegate");
                field.setAccessible(true);
                internalFactory = Utilities.convertFactory(field.get(null));
            } catch (Throwable ignore) {
                // null if inaccessible
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Error while initialising internal accessor.", ex);
        }
    }
    
    private static void breakEncapsulation(Class<?> cls) {
        Utilities.addExports(InternalAccessor.class, cls, true);
        Utilities.addOpens(InternalAccessor.class, cls, true);
        Utilities.addExports(Utilities.class, cls, true);
        Utilities.addOpens(Utilities.class, cls, true);
    }
    
}
