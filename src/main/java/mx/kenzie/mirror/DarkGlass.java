package mx.kenzie.mirror;

import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings({"unchecked", "Duplicates", "UnusedLabel"})
final class DarkGlass extends LookingGlass {
    
    final InternalAccessProvider provider;
    
    {
        try {
            provider = new InternalAccessProvider();
        } catch (ClassNotFoundException | PrivilegedActionException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Unable to access JDK internals:", e);
        }
    }
    
    //region Method Accessors
    @Override
    <Thing, Return> MethodAccessor<Return> createAccessor(Thing target, Method method) {
        final Object dark = this.createDarkAccessor(target, method);
        final Method invoker = this.getInvoker(dark);
        return super.createAccessor(dark, invoker);
    }
    
    private Object createDarkAccessor(Object target, Method method) {
        if (method == null) throw new NullPointerException("No matching method was found.");
        final String hash = "D" + method.getDeclaringClass().hashCode() + "_" + method.getName()
            .hashCode() + Objects.hash((Object[]) method.getParameterTypes());
        final Class<?> point = target instanceof Class c ? c : target.getClass();
        final String path = this.getExportedPackageFrom(point);
        Class<?> type = provider.findClass(point, path + ".Method_" + hash);
        if (type == null) {
            final String location = path.replace('.', '/') + "/Method_" + hash;
            final byte[] bytecode = this.writeDarkMethodAccessor(point, method, location);
            type = provider.loadClass(point, path + ".Method_" + hash, bytecode);
        }
        return this.make(type, target);
    }
    
    @Override
    protected void writeInvoker(ClassWriter writer, Method method, String location, Class<?> targetType) {
        final MethodVisitor visitor;
        final Type methodType = Type.getMethodType(Type.getType(Object.class), Type.getType(Object[].class));
        final Class<?>[] parameters = method.getParameterTypes();
        visitor = writer.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "invoke", methodType.getDescriptor(), null, null);
        visitor.visitCode();
        if (!Modifier.isStatic(method.getModifiers())) {
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, location, "target", "Ljava/lang/Object;");
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(targetType)); // CC is okay here, always right
        }
        visitor.visitVarInsn(ALOAD, 1);
        if (this.isReachable(method)) this.invokeNormal(visitor, method);
        else super.invokeDynamic(visitor, method);
        visitor.visitInsn(ARETURN);
        final int offset = this.wideIndexOffset(method.getParameterTypes(), method.getReturnType());
        final int size = Math.max(1 + parameters.length + offset, 4);
        visitor.visitMaxs(size, size);
        visitor.visitEnd();
    }
    
    private byte[] writeDarkMethodAccessor(Class<?> targetType, Method method, String location) {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(V17, ACC_PUBLIC | ACC_SUPER, location, null, "java/lang/Object", new String[0]);
        field:
        {
            final FieldVisitor visitor;
            visitor = writer.visitField(ACC_PROTECTED, "target", "Ljava/lang/Object;", null, null);
            visitor.visitEnd();
        }
        constructor:
        {
            final MethodVisitor visitor;
            visitor = writer.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitFieldInsn(PUTFIELD, location, "target", "Ljava/lang/Object;");
            visitor.visitInsn(RETURN);
            visitor.visitMaxs(2, 2);
            visitor.visitEnd();
        }
        if (!this.isReachable(method)) this.writeBootstrapper(writer, method);
        invoker:
        {
            final MethodVisitor visitor;
            final Type methodType = Type.getMethodType(Type.getType(Object.class), Type.getType(Object[].class));
            final Class<?>[] parameters = method.getParameterTypes();
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "invoke", methodType.getDescriptor(), null, null);
            visitor.visitCode();
            if (!Modifier.isStatic(method.getModifiers())) {
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, location, "target", "Ljava/lang/Object;");
                visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(targetType)); // CC is okay here, always right
            }
            parameters:
            {
                if (parameters.length == 0) break parameters;
                for (int i = 0; i < parameters.length; i++) {
                    this.convertParameter(visitor, parameters[i], i);
                }
            }
            if (this.isReachable(method)) this.invokeNormal(visitor, method);
            else this.invokeDynamic(visitor, method, location);
            if (method.getReturnType().isPrimitive())
                this.box(visitor, method.getReturnType());
            visitor.visitInsn(ARETURN);
            final int offset = this.wideIndexOffset(method.getParameterTypes(), method.getReturnType());
            final int size = Math.max(1 + parameters.length + offset, 4);
            visitor.visitMaxs(size, size);
            visitor.visitEnd();
        }
        return writer.toByteArray();
    }
    
    void writeBootstrapper(ClassWriter writer, Method method) {
        final MethodVisitor visitor;
        if (Modifier.isStatic(method.getModifiers())) {
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC, "bootstrapPrivate", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 3);
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            visitor.visitVarInsn(ALOAD, 3);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitVarInsn(ALOAD, 2);
            visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            visitor.visitVarInsn(ASTORE, 4);
            visitor.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
            visitor.visitInsn(DUP);
            visitor.visitVarInsn(ALOAD, 4);
            visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
            visitor.visitInsn(ARETURN);
            visitor.visitMaxs(4, 5);
        } else {
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC, "bootstrapPrivateDynamic", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 2);
            visitor.visitInsn(ICONST_0);
            visitor.visitInsn(ICONST_1);
            visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "dropParameterTypes", "(II)Ljava/lang/invoke/MethodType;", false);
            visitor.visitVarInsn(ASTORE, 5);
            visitor.visitVarInsn(ALOAD, 3);
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            visitor.visitVarInsn(ALOAD, 3);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitVarInsn(ALOAD, 5);
            visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            visitor.visitVarInsn(ASTORE, 4);
            visitor.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
            visitor.visitInsn(DUP);
            visitor.visitVarInsn(ALOAD, 4);
            visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
            visitor.visitInsn(ARETURN);
            visitor.visitMaxs(4, 6);
        }
        visitor.visitEnd();
    }
    
    void invokeDynamic(MethodVisitor visitor, Method method, String owner) {
        final Handle bootstrap;
        if (Modifier.isStatic(method.getModifiers()))
            bootstrap = Handles.createHandle(owner, "bootstrapPrivate", Type.getMethodDescriptor(Type.getType(CallSite.class), Type.getType(MethodHandles.Lookup.class), Type.getType(String.class), Type.getType(MethodType.class), Type.getType(Class.class)));
        else
            bootstrap = Handles.createHandle(owner, "bootstrapPrivateDynamic", Type.getMethodDescriptor(Type.getType(CallSite.class), Type.getType(MethodHandles.Lookup.class), Type.getType(String.class), Type.getType(MethodType.class), Type.getType(Class.class)));
        if (!Modifier.isStatic(method.getModifiers())) {
            final List<Type> adjusted = new ArrayList<>();
            for (Class<?> type : method.getParameterTypes()) {
                adjusted.add(Type.getType(type));
            }
            adjusted.add(0, Type.getType(method.getDeclaringClass()));
            visitor.visitInvokeDynamicInsn(method.getName(), Type.getMethodDescriptor(Type.getType(method.getReturnType()), adjusted.toArray(new Type[0])), bootstrap, Type.getType(method.getDeclaringClass()));
        } else {
            visitor.visitInvokeDynamicInsn(method.getName(), Type.getMethodDescriptor(method), bootstrap, Type.getType(method.getDeclaringClass()));
        }
    }
    
    private Method getInvoker(Object dark) {
        try {
            return dark.getClass().getDeclaredMethod("invoke", Object[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Provided object is not a dark invoker.", e);
        }
    }
    //endregion
    
    private String getExportedPackageFrom(Class<?> place) {
        final Module module = place.getModule();
        final Module here = LookingGlass.class.getModule();
        if (module.isExported(place.getPackageName()) || module.isExported(place.getPackageName(), here))
            return place.getPackageName();
        for (String location : module.getDescriptor().packages()) {
            if (module.isExported(location) || module.isExported(location, here)) return location;
        }
        provider.export(module, place.getPackageName());
        return place.getPackageName();
    }
    
    @Override
    <Template> Template make(Class<?> type, Object target) {
        try {
            final Constructor<Template> constructor = (Constructor<Template>) type.getConstructor(Object.class);
            return provider.newInstance(constructor, target);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("An impossible state has been met during frame creation.", e);
        }
    }
    
    @Override
    boolean verify() {
        return false;
    }
    
}
