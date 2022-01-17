package mx.kenzie.mirror;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.*;
import java.security.PrivilegedActionException;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings({"unchecked", "Duplicates", "UnusedLabel", "TypeParameterHidesVisibleType"})
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
    
    private Method getInvoker(Object dark) {
        try {
            return dark.getClass().getDeclaredMethod("invoke", Object[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Provided object is not a dark invoker.", e);
        }
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
        if (!isReachable(method)) this.writeBootstrapper(writer, method);
        invoker:
        // can't super the invoker since we need to use local bootstrap
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
            if (isReachable(method)) this.invokeNormal(visitor, method);
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
    
    @Override
    protected void writeInvoker(ClassWriter writer, Method method, String location, Class<?> targetType) {
        final MethodVisitor visitor;
        final Type methodType = Type.getMethodType(Type.getType(Object.class), Type.getType(Object[].class));
        final Class<?>[] parameters = method.getParameterTypes();
        visitor = writer.visitMethod(ACC_PUBLIC | ACC_BRIDGE, "invoke", methodType.getDescriptor(), null, null);
        visitor.visitCode();
        if (!Modifier.isStatic(method.getModifiers())) {
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, location, "target", "Ljava/lang/Object;");
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(targetType)); // CC is okay here, always right
        }
        if (verify()) {
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitIntInsn(BIPUSH, parameters.length);
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(MethodAccessor.MethodAccessorImpl.class), "verifyArray", "([Ljava/lang/Object;I)V", false);
        }
        parameters:
        {
            if (parameters.length == 0) break parameters;
            if (parameters[0] == Object[].class) {
                visitor.visitVarInsn(ALOAD, 1); // pass array directly since it's a bridge
                break parameters;
            }
            for (int i = 0; i < parameters.length; i++) {
                this.convertParameter(visitor, parameters[i], i);
            }
        }
        if (isReachable(method)) this.invokeNormal(visitor, method);
        else this.invokeDynamic(visitor, method);
        if (method.getReturnType().isPrimitive())
            this.box(visitor, method.getReturnType());
        visitor.visitInsn(ARETURN);
        final int offset = this.wideIndexOffset(method.getParameterTypes(), method.getReturnType());
        final int size = Math.max(1 + parameters.length + offset, 4);
        visitor.visitMaxs(size, size);
        visitor.visitEnd();
    }
    //endregion
    
    //region Field Accessors
    @Override
    <
        Thing,
        Type>
    FieldAccessor<Type> createAccessor(Thing target, Field field) {
        final Object dark = this.createDarkAccessor(target, field);
        final Method getter = this.getFieldGetter(dark);
        final Method setter = this.getFieldSetter(dark);
        return new FieldAccessor.RelayedFieldAccessor<>(super.createAccessor(dark, getter), super.createAccessor(dark, setter));
    }
    
    private Object createDarkAccessor(Object target, Field field) {
        if (field == null) throw new NullPointerException("No matching field was found.");
        final String hash = "D" + field.getDeclaringClass().hashCode() + "_" + field.getName()
            .hashCode() + Objects.hash(field.getType());
        final Class<?> point = target instanceof Class c ? c : target.getClass();
        final String path = this.getExportedPackageFrom(point);
        Class<?> type = provider.findClass(point, path + ".Field_" + hash);
        if (type == null) {
            final String location = path.replace('.', '/') + "/Field_" + hash;
            final byte[] bytecode = this.writeDarkFieldAccessor(point, field, location);
            type = provider.loadClass(point, path + ".Field_" + hash, bytecode);
        }
        return this.make(type, target);
    }
    
    private Method getFieldGetter(Object dark) {
        try {
            return dark.getClass().getDeclaredMethod("get");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Provided object is not a dark field accessor.", e);
        }
    }
    
    private Method getFieldSetter(Object dark) {
        try {
            return dark.getClass().getDeclaredMethod("set", Object.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Provided object is not a dark field accessor.", e);
        }
    }
    
    private byte[] writeDarkFieldAccessor(Class<?> targetType, Field field, String location) {
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
        if (!isReachable(field)) this.writeBootstrapper(writer, field);
        setter:
        {
            final MethodVisitor visitor;
            final Class<?> type = field.getType();
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "set", "(Ljava/lang/Object;)V", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, location, "target", "Ljava/lang/Object;");
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(targetType));
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(this.getWrapperType(type)));
            if (type.isPrimitive()) this.unbox(visitor, type);
            if (isReachable(field)) this.setNormal(visitor, field);
            else this.setDynamic(visitor, field, location);
            visitor.visitInsn(RETURN);
            final int offset = this.wideIndexOffset(field.getType());
            final int size = 3 + offset;
            visitor.visitMaxs(size, size);
            visitor.visitEnd();
        }
        getter:
        {
            final MethodVisitor visitor;
            final Class<?> type = field.getType();
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "get", "()Ljava/lang/Object;", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, location, "target", "Ljava/lang/Object;");
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(targetType));
            if (isReachable(field)) this.getNormal(visitor, field);
            else this.getDynamic(visitor, field, location);
            if (type.isPrimitive()) this.box(visitor, type);
            visitor.visitInsn(ARETURN);
            final int offset = this.wideIndexOffset(field.getType());
            final int size = 2 + offset;
            visitor.visitMaxs(size, size);
            visitor.visitEnd();
        }
        return writer.toByteArray();
    }
    //endregion
    
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
