package mx.kenzie.mirror;

import mx.kenzie.mimic.MimicGenerator;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

class IntrinsicMimicGenerator extends MimicGenerator {
    
    protected final Mirror<?> mirror;
    private boolean hasStaticMethodBootstrap;
    private boolean hasDynamicMethodBootstrap;
    private boolean hasStaticFieldBootstrap;
    private boolean hasDynamicFieldBootstrap;
    
    protected IntrinsicMimicGenerator(String location, Class<?> top, Mirror<?> mirror) {
        super(location, top);
        this.mirror = mirror;
    }
    
    public <Template> Template createInline() {
        final boolean complex = !top.isInterface();
        final byte[] bytecode = writeCode();
        if (mirror.glass.provider instanceof InternalAccessProvider provider) {
            final Class<?> target = mirror.emergentClass();
            provider.assureAvailable(top, target);
            provider.export(target.getModule(), target.getPackageName());
        }
        final Class<?> type = mirror.glass.loadClass(top, internal.replace('/', '.'), bytecode);
        final Object object = this.allocateInstance(type);
        if (complex) {
            try {
                final long offset = this.offset(object.getClass().getDeclaredField("target"));
                this.putValue(object, offset, mirror.target);
            } catch (NoSuchFieldException ignored) {
            }
        } else {
            this.putValue(object, 12, mirror.target);
        }
        return (Template) object;
    }
    
    @Override
    protected byte[] writeCode() {
        writer.visit(61, 1 | 16 | 32, internal, null, Type.getInternalName(top != null && !top.isInterface() ? top : Object.class), this.getInterfaces());
        target:
        {
            final FieldVisitor visitor = writer.visitField(ACC_PUBLIC, "target", mirror.emergentClass()
                .descriptorString(), null, null);
            visitor.visitEnd();
        }
        methods:
        {
            if (top == null || top == Object.class) break methods;
            this.scrapeMethods(top);
        }
        writer.visitEnd();
        return writer.toByteArray();
    }
    
    @Override
    protected void writeCaller(Method method) {
        final Method target = mirror.findMethod(method.getName(), method.getParameterTypes());
        if (target == null && method.getName().startsWith("$")) this.writeFieldCaller(method);
        if (target == null) return;
        if (!LookingGlass.isReachable(target)) this.writeBootstrapper(writer, target);
        final MethodVisitor visitor = writer.visitMethod(1 | 16 | 4096, method.getName(), Type.getMethodDescriptor(method), null, null);
        visitor.visitCode();
        if (!Modifier.isStatic(target.getModifiers())) {
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, internal, "target", mirror.emergentClass().descriptorString());
        }
        int index = 0;
        for (Class<?> type : target.getParameterTypes()) {
            visitor.visitVarInsn(20 + instructionOffset(type), ++index);
            if (type == long.class || type == double.class) index++;
        }
        if (LookingGlass.isReachable(target)) mirror.glass.invokeNormal(visitor, target);
        else mirror.glass.invokeDynamic(visitor, target, internal);
        if (method.getReturnType() == void.class && target.getReturnType() == void.class) {
            visitor.visitInsn(RETURN);
        } else if (method.getReturnType() == void.class) {
            visitor.visitInsn(POP);
            visitor.visitInsn(RETURN);
        } else if (target.getReturnType() == method.getReturnType()) {
            visitor.visitInsn(171 + instructionOffset(method.getReturnType()));
        } else {
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(getWrapperType(method.getReturnType())));
            this.unbox(visitor, method.getReturnType());
            visitor.visitInsn(171 + instructionOffset(method.getReturnType()));
        }
        final int offset = Math.max(this.wideIndexOffset(method.getParameterTypes(), method.getReturnType()), this.wideIndexOffset(target.getParameterTypes(), target.getReturnType()));
        final int size = 1 + target.getParameterCount() + offset;
        visitor.visitMaxs(size, size);
        visitor.visitEnd();
    }
    
    protected void writeFieldCaller(Method method) {
        final String name = method.getName().substring(1);
        final Field target = mirror.findField(name);
        if (target == null) return;
        if (!LookingGlass.isReachable(target)) this.writeBootstrapper(writer, target);
        final MethodVisitor visitor = writer.visitMethod(1 | 16 | 4096, method.getName(), Type.getMethodDescriptor(method), null, null);
        visitor.visitCode();
        if (method.getParameterCount() > 0) {
            if (!Modifier.isStatic(target.getModifiers())) {
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, internal, "target", mirror.emergentClass().descriptorString());
            }
            final Class<?> type = method.getParameterTypes()[0];
            visitor.visitVarInsn(20 + instructionOffset(type), 1);
            if (type != target.getType() && !type.isPrimitive() && !target.getType().isPrimitive())
                visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(target.getType()));
            if (LookingGlass.isReachable(target)) mirror.glass.setNormal(visitor, target);
            else mirror.glass.setDynamic(visitor, target, internal);
        }
        if (method.getReturnType() == void.class) {
            visitor.visitInsn(RETURN);
        } else {
            if (!Modifier.isStatic(target.getModifiers())) {
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, internal, "target", mirror.emergentClass().descriptorString());
            }
            if (LookingGlass.isReachable(target)) mirror.glass.getNormal(visitor, target);
            else mirror.glass.getDynamic(visitor, target, internal);
            visitor.visitInsn(171 + instructionOffset(method.getReturnType()));
        }
        final int offset = Math.max(this.wideIndexOffset(method.getParameterTypes(), method.getReturnType()), this.wideIndexOffset(target.getType()));
        final int size = 2 + offset;
        visitor.visitMaxs(size, size);
        visitor.visitEnd();
    }
    
    private void writeBootstrapper(ClassWriter writer, Method method) {
        final boolean dynamic = !Modifier.isStatic(method.getModifiers());
        if (dynamic && hasDynamicMethodBootstrap) return;
        else if (!dynamic && hasStaticMethodBootstrap) return;
        if (dynamic) hasDynamicMethodBootstrap = true;
        else hasStaticMethodBootstrap = true;
        mirror.glass.writeBootstrapper(writer, method);
    }
    
    private void writeBootstrapper(ClassWriter writer, Field field) {
        final boolean dynamic = !Modifier.isStatic(field.getModifiers());
        if (dynamic && hasDynamicFieldBootstrap) return;
        else if (!dynamic && hasStaticFieldBootstrap) return;
        if (dynamic) hasDynamicFieldBootstrap = true;
        else hasStaticFieldBootstrap = true;
        mirror.glass.writeBootstrapper(writer, field);
    }
    
}
