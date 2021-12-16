package mx.kenzie.mirror;

import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.security.*;

@SuppressWarnings("all")
class InternalAccessProvider implements ClassProvider {
    
    final Object javaLangAccess;
    final Unsafe unsafe;
    final Method defineClass;
    final Method addExports0;
    final Method newInstanceWithCaller;
    
    InternalAccessProvider() throws ClassNotFoundException, PrivilegedActionException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class<?> secrets = Class.forName("jdk.internal.access.SharedSecrets", false, ClassLoader.getSystemClassLoader());
        unsafe = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        });
        final Field field = Class.class.getDeclaredField("module");
        final long offset = unsafe.objectFieldOffset(field);
        unsafe.putObject(InternalAccessProvider.class, offset, Object.class.getModule());
        final Method setAccessible0 = AccessibleObject.class.getDeclaredMethod("setAccessible0", boolean.class);
        setAccessible0.setAccessible(true);
        final Method implAddExportsOrOpens = Module.class.getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class);
        setAccessible0.invoke(implAddExportsOrOpens, true);
        addExports0 = Module.class.getDeclaredMethod("addExports0", Module.class, String.class, Module.class);
        setAccessible0.invoke(addExports0, true);
        final Method getJavaLangAccess = secrets.getDeclaredMethod("getJavaLangAccess");
        setAccessible0.invoke(getJavaLangAccess, true);
        javaLangAccess = getJavaLangAccess.invoke(null);
        defineClass = javaLangAccess.getClass()
            .getMethod("defineClass", ClassLoader.class, String.class, byte[].class, ProtectionDomain.class, String.class);
        setAccessible0.invoke(defineClass, true);
        newInstanceWithCaller = Constructor.class.getDeclaredMethod("newInstanceWithCaller", Object[].class, boolean.class, Class.class);
        setAccessible0.invoke(newInstanceWithCaller, true);
    }
    
    Class<?> findClass(Class<?> target, String name) {
        final Module module = target.getModule();
        PrivilegedAction<ClassLoader> pa = module::getClassLoader;
        final ClassLoader loader = AccessController.doPrivileged(pa);
        try {
            return Class.forName(name, true, loader);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
    @Override
    public Class<?> loadClass(Class<?> target, String name, byte[] bytes) {
        try {
            final Module module = target.getModule();
            PrivilegedAction<ClassLoader> pa = module::getClassLoader;
            final ClassLoader loader = AccessController.doPrivileged(pa);
            final Class<?> type = (Class<?>) defineClass.invoke(javaLangAccess, loader, name, bytes, null, "__Mirror__");
            return type;
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    void export(final Module module, final String namespace) {
        try {
            addExports0.invoke(null, module, namespace, LookingGlass.class.getModule());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            module.addExports(namespace, LookingGlass.class.getModule());
        }
    }
    
    <Type> Type newInstance(Constructor<Type> constructor, Object... parameters) {
        try {
            return (Type) this.newInstanceWithCaller.invoke(constructor, parameters, false, constructor.getDeclaringClass());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
