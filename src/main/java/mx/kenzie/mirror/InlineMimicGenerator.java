package mx.kenzie.mirror;

import mx.kenzie.mimic.MimicGenerator;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

class InlineMimicGenerator extends MimicGenerator {
    
    protected List<MethodAccessor<?>> accessors = new ArrayList<>();
    protected final Mirror<?> mirror;
    
    protected InlineMimicGenerator(String location, Class<?> top, Mirror<?> mirror) {
        super(location, top);
        this.mirror = mirror;
    }
    
    static String getStrictPackageName(Class<?> top, Class<?>... interfaces) {
        String namespace = "com.sun.proxy.mimic"; // Proxies get special treatment for going here so need to use this
        if (top != null && !Modifier.isPublic(top.getModifiers())) {
            namespace = top.getPackageName();
        }
        for (Class<?> place : interfaces) {
            if (!Modifier.isPublic(place.getModifiers())) {
                namespace = place.getPackageName();
            }
        }
        return namespace;
    }
    
    public <Template> Template createInline(LookingGlass glass) {
        final boolean complex = !top.isInterface();
        final byte[] bytecode = writeCode();
        final Class<?> type = glass.loadClass(top, internal.replace('/', '.'), bytecode);
        final Object object = this.allocateInstance(type);
        if (complex) {
            try {
                final long offset = this.offset(object.getClass().getDeclaredField("methods"));
                this.putValue(object, offset, accessors.toArray(new MethodAccessor[0]));
            } catch (NoSuchFieldException ignored) {
            }
        } else {
            this.putValue(object, 12, accessors.toArray(new MethodAccessor[0]));
        }
        return (Template) object;
    }
    
    @Override
    protected byte[] writeCode() {
        writer.visit(61, 1 | 16 | 32, internal, null, Type.getInternalName(top != null && !top.isInterface() ? top : Object.class), this.getInterfaces());
        method_accessors:
        {
            final FieldVisitor visitor = writer.visitField(0, "methods", "[Lmx/kenzie/mirror/MethodAccessor;", null, null);
            visitor.visitEnd();
        }
        super_methods:
        {
            if (top == null || top == Object.class) break super_methods;
            this.scrapeMethods(top);
        }
        writer.visitEnd();
        return writer.toByteArray();
    }
    
    @Override
    protected void writeCaller(Method method) {
        final MethodAccessor<?> accessor = mirror.method(method.getName(), method.getParameterTypes());
        this.accessors.add(accessor);
        final MethodVisitor visitor = writer.visitMethod(1 | 16 | 4096, method.getName(), Type.getMethodDescriptor(method), null, null);
        visitor.visitCode();
        visitor.visitVarInsn(ALOAD, 0);
        visitor.visitFieldInsn(GETFIELD, internal, "methods", "[Lmx/kenzie/mirror/MethodAccessor;");
        visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(MethodAccessor[].class));
        visitor.visitIntInsn(BIPUSH, index);
        visitor.visitInsn(AALOAD);
        visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(MethodAccessor.class));
        visitor.visitIntInsn(BIPUSH, method.getParameterCount());
        visitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        int argumentIndex = 0;
        int storeIndex = -1;
        for (Class<?> parameter : method.getParameterTypes()) {
            visitor.visitInsn(DUP);
            visitor.visitIntInsn(BIPUSH, ++storeIndex);
            visitor.visitVarInsn(20 + instructionOffset(parameter), ++argumentIndex);
            this.box(visitor, parameter);
            visitor.visitInsn(AASTORE);
            argumentIndex += wideIndexOffset(parameter);
        }
        visitor.visitMethodInsn(INVOKEINTERFACE, "mx/kenzie/mirror/MethodAccessor", "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", true);
        if (method.getReturnType() == void.class) {
            visitor.visitInsn(POP);
            visitor.visitInsn(RETURN);
        } else {
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(getWrapperType(method.getReturnType())));
            this.unbox(visitor, method.getReturnType());
            visitor.visitInsn(171 + instructionOffset(method.getReturnType()));
        }
        visitor.visitMaxs(7, 1 + method.getParameterCount() + wideIndexOffset(method.getParameterTypes(), method.getReturnType()));
        visitor.visitEnd();
    }
}
