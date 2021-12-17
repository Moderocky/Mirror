package mx.kenzie.mirror;

import mx.kenzie.glass.Glass;
import mx.kenzie.glass.Window;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings({"unchecked", "Duplicates", "UnusedLabel", "TypeParameterHidesVisibleType"})
class LookingGlass extends Glass implements ClassProvider {
    
    protected ClassProvider provider;
    
    {
        try {
            provider = new InternalAccessProvider();
        } catch (Throwable ex) {
            provider = null;
        }
    }
    
    //region Constructor Accessor Generation
    <Thing>
    ConstructorAccessor<Thing> createAccessor(Class<?> target, Constructor<?> constructor) {
        if (constructor == null) throw new NullPointerException("No matching constructor was found.");
        final String hash = "" + constructor.getName()
            .hashCode() + Objects.hash((Object[]) constructor.getParameterTypes());
        final Class<?> type;
        if (cache.containsKey(hash)) type = cache.get(hash);
        else {
            final String path = this.getExportedPackageFrom(target);
            final String location = path.replace('.', '/') + "/Method_" + hash;
            final byte[] bytecode = this.writeConstructorAccessor(target, constructor, location);
            type = this.loadClass(target, path + ".Method_" + hash, bytecode);
            cache.put(hash, (Class<? extends Window.WindowFrame>) type);
        }
        final ConstructorAccessor.ConstructorAccessorImpl<Thing> accessor = this.make(type, target);
        accessor.modifiers = constructor.getModifiers();
        accessor.dynamic = !this.isReachable(constructor);
        return accessor;
    }
    
    byte[] writeConstructorAccessor(Class<?> targetType, Constructor<?> constructor, String location) {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(V17, ACC_PUBLIC | ACC_SUPER, location, null, Type.getInternalName(ConstructorAccessor.ConstructorAccessorImpl.class), new String[]{Type.getInternalName(MethodAccessor.class)});
        constructor:
        {
            final MethodVisitor visitor;
            visitor = writer.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(ConstructorAccessor.ConstructorAccessorImpl.class), "<init>", "(Ljava/lang/Object;)V", false);
            visitor.visitInsn(RETURN);
            visitor.visitMaxs(2, 2);
            visitor.visitEnd();
        }
        invoker:
        {
            final MethodVisitor visitor;
            final Type methodType = Type.getMethodType(Type.getType(Object.class), Type.getType(Object[].class));
            final Class<?>[] parameters = constructor.getParameterTypes();
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "invoke", methodType.getDescriptor(), null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitIntInsn(BIPUSH, parameters.length);
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(ConstructorAccessor.ConstructorAccessorImpl.class), "verifyArray", "([Ljava/lang/Object;I)V", false);
            if (this.isReachable(constructor)) {
                visitor.visitTypeInsn(NEW, Type.getInternalName(targetType));
                visitor.visitInsn(DUP);
            }
            parameters:
            {
                if (parameters.length == 0) break parameters;
                for (int i = 0; i < parameters.length; i++) {
                    this.convertParameter(visitor, parameters[i], i);
                }
            }
            if (this.isReachable(constructor)) this.invokeSpecial(visitor, constructor);
            else this.invokeDynamic(visitor, constructor);
            visitor.visitInsn(ARETURN);
            final int offset = this.wideIndexOffset(constructor.getParameterTypes(), constructor.getDeclaringClass());
            final int size = Math.max(2 + parameters.length + offset, 5);
            visitor.visitMaxs(size, size);
            visitor.visitEnd();
        }
        return writer.toByteArray();
    }
    
    Constructor<?> findConstructor(Class<?> target, Class<?>... parameters) {
        try {
            return target.getDeclaredConstructor(parameters);
        } catch (Throwable ex) {
            return null;
        }
    }
    //endregion
    
    //region Method Accessor Generation
    <
        Thing,
        Return>
    MethodAccessor<Return> createAccessor(Thing target, Method method) {
        if (method == null) throw new NullPointerException("No matching method was found.");
        final String hash = "" + method.getDeclaringClass().hashCode() + "_" + method.getName()
            .hashCode() + Objects.hash((Object[]) method.getParameterTypes());
        final Class<?> point = target instanceof Class c ? c : target.getClass();
        final Class<?> type;
        if (cache.containsKey(hash)) type = cache.get(hash);
        else {
            final String path = this.getExportedPackageFrom(point);
            final String location = path.replace('.', '/') + "/Method_" + hash;
            final byte[] bytecode = this.writeMethodAccessor(point, method, location);
            type = this.loadClass(LookingGlass.class, path + ".Method_" + hash, bytecode);
            cache.put(hash, (Class<? extends Window.WindowFrame>) type);
        }
        final MethodAccessor.MethodAccessorImpl<Thing, Return> accessor = this.make(type, target);
        accessor.modifiers = method.getModifiers();
        accessor.dynamic = !this.isReachable(method);
        return accessor;
    }
    
    byte[] writeMethodAccessor(Class<?> targetType, Method method, String location) {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(V17, ACC_PUBLIC | ACC_SUPER, location, null, Type.getInternalName(MethodAccessor.MethodAccessorImpl.class), new String[]{Type.getInternalName(MethodAccessor.class)});
        constructor:
        {
            final MethodVisitor visitor;
            visitor = writer.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(MethodAccessor.MethodAccessorImpl.class), "<init>", "(Ljava/lang/Object;)V", false);
            visitor.visitInsn(RETURN);
            visitor.visitMaxs(2, 2);
            visitor.visitEnd();
        }
        if (!this.isReachable(method)) this.writeBootstrapper(writer, method);
        this.writeInvoker(writer, method, location, targetType);
        return writer.toByteArray();
    }
    
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
    
    void convertParameter(MethodVisitor visitor, Class<?> parameter, int index) {
        visitor.visitVarInsn(ALOAD, 1);
        visitor.visitIntInsn(BIPUSH, index);
        visitor.visitInsn(AALOAD);
        visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(this.getWrapperType(parameter))); // CC is okay here
        if (parameter.isPrimitive())
            unbox(visitor, parameter);
    }
    
    void invokeNormal(MethodVisitor visitor, Method method) {
        if (Modifier.isInterface(method.getDeclaringClass().getModifiers())) {
            visitor.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), true);
        } else if (Modifier.isStatic(method.getModifiers())) {
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), false);
        } else {
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), false);
        }
    }
    
    private void invokeSpecial(MethodVisitor visitor, Constructor<?> constructor) {
        visitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(constructor.getDeclaringClass()), "<init>", Type.getConstructorDescriptor(constructor), false);
    }
    
    private void invokeDynamic(MethodVisitor visitor, Constructor<?> constructor) {
        final Handle bootstrap = Handles.getBootstrap(constructor);
        final List<Type> adjusted = new ArrayList<>();
        for (Class<?> type : constructor.getParameterTypes()) {
            adjusted.add(Type.getType(type));
        }
        visitor.visitInvokeDynamicInsn("constructor", Type.getMethodDescriptor(Type.getType(constructor.getDeclaringClass()), adjusted.toArray(new Type[0])), bootstrap, Type.getType(constructor.getDeclaringClass()));
    }
    
    void invokeDynamic(MethodVisitor visitor, Method method) {
        final Handle bootstrap = Handles.getBootstrap(method);
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
    
    Method findMethod(Class<?> target, String name, Class<?>... parameters) {
        try {
            return target.getDeclaredMethod(name, parameters);
        } catch (NoSuchMethodException ex) {
            if (target == Object.class) return null;
            return findMethod(target.getSuperclass(), name, parameters);
        } catch (Throwable ex) {
            return null;
        }
    }
    //endregion
    
    void invokeDynamic(MethodVisitor visitor, Method method, String owner) {
        final Handle bootstrap = Handles.createHandle(owner, "bootstrapInvoke", Type.getMethodDescriptor(Type.getType(CallSite.class), Type.getType(MethodHandles.Lookup.class), Type.getType(String.class), Type.getType(MethodType.class), Type.getType(Class.class)));
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
    
    //region Field Accessor Generation
    <
        Thing,
        Type>
    FieldAccessor<Type> createAccessor(Thing target, Field field) {
        if (field == null) throw new NullPointerException("No matching field was found.");
        final String hash = "" + field.getDeclaringClass().hashCode() + "_" + field.getName()
            .hashCode() + field.getType().hashCode();
        final Class<?> type;
        if (cache.containsKey(hash)) type = cache.get(hash);
        else {
            final Class<?> point = target instanceof Class c ? c : target.getClass();
            final String path = this.getExportedPackageFrom(point);
            final String location = path.replace('.', '/') + "/Field_" + hash;
            final byte[] bytecode = this.writeFieldAccessor(point, field, location);
            type = this.loadClass(point, path + ".Field_" + hash, bytecode);
            cache.put(hash, (Class<? extends Window.WindowFrame>) type);
        }
        final FieldAccessor.FieldAccessorImpl<Thing, Type> accessor = this.make(type, target);
        accessor.modifiers = field.getModifiers();
        accessor.dynamic = !this.isReachable(field);
        accessor.type = (Class<Type>) field.getType();
        return accessor;
    }
    
    byte[] writeFieldAccessor(Class<?> targetType, Field field, String location) {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(V17, ACC_PUBLIC | ACC_SUPER, location, null, Type.getInternalName(FieldAccessor.FieldAccessorImpl.class), new String[]{Type.getInternalName(FieldAccessor.class)});
        constructor:
        {
            final MethodVisitor visitor;
            visitor = writer.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
            visitor.visitCode();
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(FieldAccessor.FieldAccessorImpl.class), "<init>", "(Ljava/lang/Object;)V", false);
            visitor.visitInsn(RETURN);
            visitor.visitMaxs(2, 2);
            visitor.visitEnd();
        }
        if (!this.isReachable(field)) this.writeBootstrapper(writer, field);
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
            if (this.isReachable(field)) this.setNormal(visitor, field);
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
            if (this.isReachable(field)) this.getNormal(visitor, field);
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
    
    void setNormal(MethodVisitor visitor, Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            visitor.visitFieldInsn(PUTSTATIC, Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(field.getType()));
        } else {
            visitor.visitFieldInsn(PUTFIELD, Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(field.getType()));
        }
    }
    
    void setDynamic(MethodVisitor visitor, Field field) {
        final Handle bootstrap = Handles.getBootstrap(field, true);
        final Type[] types;
        if (Modifier.isStatic(field.getModifiers())) types = new Type[]{Type.getType(field.getType())};
        else types = new Type[]{Type.getType(field.getDeclaringClass()), Type.getType(field.getType())};
        visitor.visitInvokeDynamicInsn(field.getName(), Type.getMethodDescriptor(Type.getType(void.class), types), bootstrap, Type.getType(field.getDeclaringClass()));
    }
    
    void getNormal(MethodVisitor visitor, Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            visitor.visitFieldInsn(GETSTATIC, Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(field.getType()));
        } else {
            visitor.visitFieldInsn(GETFIELD, Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(field.getType()));
        }
    }
    
    void getDynamic(MethodVisitor visitor, Field field) {
        final Handle bootstrap = Handles.getBootstrap(field, false);
        final String descriptor;
        if (Modifier.isStatic(field.getModifiers()))
            descriptor = Type.getMethodDescriptor(Type.getType(field.getType()));
        else
            descriptor = Type.getMethodDescriptor(Type.getType(field.getType()), Type.getType(field.getDeclaringClass()));
        visitor.visitInvokeDynamicInsn(field.getName(), descriptor, bootstrap, Type.getType(field.getDeclaringClass()));
    }
    
    Field findField(Class<?> target, String name) {
        try {
            return target.getDeclaredField(name);
        } catch (NoSuchFieldException ex) {
            if (target == Object.class) return null;
            return findField(target.getSuperclass(), name);
        } catch (Throwable ex) {
            return null;
        }
    }
    //endregion
    
    void getDynamic(MethodVisitor visitor, Field field, String owner) {
        final Handle bootstrap = Handles.createHandle(owner, "bootstrapGetter", Type.getMethodDescriptor(Type.getType(CallSite.class), Type.getType(MethodHandles.Lookup.class), Type.getType(String.class), Type.getType(MethodType.class), Type.getType(Class.class)));
        final String descriptor;
        if (Modifier.isStatic(field.getModifiers()))
            descriptor = Type.getMethodDescriptor(Type.getType(field.getType()));
        else
            descriptor = Type.getMethodDescriptor(Type.getType(field.getType()), Type.getType(field.getDeclaringClass()));
        visitor.visitInvokeDynamicInsn(field.getName(), descriptor, bootstrap, Type.getType(field.getDeclaringClass()));
    }
    
    void setDynamic(MethodVisitor visitor, Field field, String owner) {
        final Handle bootstrap = Handles.createHandle(owner, "bootstrapSetter", Type.getMethodDescriptor(Type.getType(CallSite.class), Type.getType(MethodHandles.Lookup.class), Type.getType(String.class), Type.getType(MethodType.class), Type.getType(Class.class)));
        final Type[] types;
        if (Modifier.isStatic(field.getModifiers())) types = new Type[]{Type.getType(field.getType())};
        else types = new Type[]{Type.getType(field.getDeclaringClass()), Type.getType(field.getType())};
        visitor.visitInvokeDynamicInsn(field.getName(), Type.getMethodDescriptor(Type.getType(void.class), types), bootstrap, Type.getType(field.getDeclaringClass()));
    }
    
    //region Creators
    <Template> Template make(Class<?> type, Object target) {
        try {
            final Constructor<Template> constructor = (Constructor<Template>) type.getConstructor(Object.class);
            return constructor.newInstance(target);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("An impossible state has been met during frame creation.", e);
        }
    }
    
    <Template, Thing> Template makeProxy(Mirror<Thing> mirror, Class<Template> template) {
        final ClassLoader loader;
        if (template.getClassLoader() == null) loader = this.loader;
        else loader = template.getClassLoader();
        return (Template) Proxy.newProxyInstance(loader, new Class[]{template}, new ProxyAccessor<>(mirror));
    }
    //endregion
    
    //region Boilerplate
    boolean isReachable(Object thing) {
        if (thing instanceof Class<?> object) return Modifier.isPublic(object.getModifiers());
        else if (thing instanceof Method object)
            return Modifier.isPublic(object.getModifiers()) && this.isReachable(object.getDeclaringClass());
        else if (thing instanceof Field object)
            return Modifier.isPublic(object.getModifiers()) && this.isReachable(object.getDeclaringClass());
        else if (thing instanceof Constructor object)
            return Modifier.isPublic(object.getModifiers()) && this.isReachable(object.getDeclaringClass());
        else return this.isReachable(thing.getClass());
    }
    
    boolean verify() {
        return true;
    }
    
    ClassProvider getProvider() {
        return provider;
    }
    //endregion
    
    //region Class Loaders
    @Override
    public Class<?> loadClass(Class<?> target, String name, byte[] bytes) {
        if (getProvider() == this) return super.loadClass(name, bytes);
        else return getProvider().loadClass(target, name, bytes);
    }
    
    @Override
    protected Class<?> loadClass(String name, byte[] bytes) {
        return this.loadClass(LookingGlass.class, name, bytes);
    }
    //endregion
    
    //region Boxing
    Class<?> getWrapperType(Class<?> primitive) {
        if (primitive == byte.class) return Byte.class;
        if (primitive == short.class) return Short.class;
        if (primitive == int.class) return Integer.class;
        if (primitive == long.class) return Long.class;
        if (primitive == float.class) return Float.class;
        if (primitive == double.class) return Double.class;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == void.class) return Void.class;
        return primitive;
    }
    
    void unbox(MethodVisitor visitor, Class<?> parameter) {
        if (parameter == byte.class)
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Byte.class), "byteValue", "()B", false);
        if (parameter == short.class)
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Short.class), "shortValue", "()S", false);
        if (parameter == int.class)
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Integer.class), "intValue", "()I", false);
        if (parameter == long.class)
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Long.class), "longValue", "()J", false);
        if (parameter == float.class)
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Float.class), "floatValue", "()F", false);
        if (parameter == double.class)
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Double.class), "doubleValue", "()D", false);
        if (parameter == boolean.class)
            visitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Boolean.class), "booleanValue", "()Z", false);
    }
    
    void box(MethodVisitor visitor, Class<?> value) {
        if (value == byte.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;", false);
        if (value == short.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;", false);
        if (value == int.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
        if (value == long.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;", false);
        if (value == float.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;", false);
        if (value == double.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;", false);
        if (value == boolean.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);
        if (value == void.class)
            visitor.visitInsn(ACONST_NULL);
    }
    //endregion
    
    //region Bootstrap Methods
    protected void writeBootstrapper(ClassWriter writer, Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            setter:
            {
                final MethodVisitor visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC, "bootstrapSetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
                visitor.visitCode();
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 1);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitInsn(ICONST_0);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "parameterType", "(I)Ljava/lang/Class;", false);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticVarHandle", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;", false);
                visitor.visitFieldInsn(GETSTATIC, "java/lang/invoke/VarHandle$AccessMode", "SET", "Ljava/lang/invoke/VarHandle$AccessMode;");
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/VarHandle", "toMethodHandle", "(Ljava/lang/invoke/VarHandle$AccessMode;)Ljava/lang/invoke/MethodHandle;", false);
                visitor.visitVarInsn(ASTORE, 4);
                visitor.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
                visitor.visitInsn(DUP);
                visitor.visitVarInsn(ALOAD, 4);
                visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
                visitor.visitInsn(ARETURN);
                visitor.visitMaxs(5, 5);
                visitor.visitEnd();
            }
            getter:
            {
                final MethodVisitor visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC, "bootstrapGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
                visitor.visitCode();
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 1);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "returnType", "()Ljava/lang/Class;", false);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticVarHandle", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;", false);
                visitor.visitFieldInsn(GETSTATIC, "java/lang/invoke/VarHandle$AccessMode", "GET", "Ljava/lang/invoke/VarHandle$AccessMode;");
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/VarHandle", "toMethodHandle", "(Ljava/lang/invoke/VarHandle$AccessMode;)Ljava/lang/invoke/MethodHandle;", false);
                visitor.visitVarInsn(ASTORE, 4);
                visitor.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
                visitor.visitInsn(DUP);
                visitor.visitVarInsn(ALOAD, 4);
                visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
                visitor.visitInsn(ARETURN);
                visitor.visitMaxs(4, 5);
                visitor.visitEnd();
            }
        } else {
            setter:
            {
                final MethodVisitor visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, "bootstrapSetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
                visitor.visitCode();
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 1);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitInsn(ICONST_1);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "parameterType", "(I)Ljava/lang/Class;", false);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVarHandle", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;", false);
                visitor.visitFieldInsn(GETSTATIC, "java/lang/invoke/VarHandle$AccessMode", "SET", "Ljava/lang/invoke/VarHandle$AccessMode;");
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/VarHandle", "toMethodHandle", "(Ljava/lang/invoke/VarHandle$AccessMode;)Ljava/lang/invoke/MethodHandle;", false);
                visitor.visitVarInsn(ASTORE, 4);
                visitor.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
                visitor.visitInsn(DUP);
                visitor.visitVarInsn(ALOAD, 4);
                visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
                visitor.visitInsn(ARETURN);
                visitor.visitMaxs(5, 5);
                visitor.visitEnd();
            }
            getter:
            {
                final MethodVisitor visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, "bootstrapGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
                visitor.visitCode();
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 1);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "returnType", "()Ljava/lang/Class;", false);
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVarHandle", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;", false);
                visitor.visitFieldInsn(GETSTATIC, "java/lang/invoke/VarHandle$AccessMode", "GET", "Ljava/lang/invoke/VarHandle$AccessMode;");
                visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/VarHandle", "toMethodHandle", "(Ljava/lang/invoke/VarHandle$AccessMode;)Ljava/lang/invoke/MethodHandle;", false);
                visitor.visitVarInsn(ASTORE, 4);
                visitor.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
                visitor.visitInsn(DUP);
                visitor.visitVarInsn(ALOAD, 4);
                visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
                visitor.visitInsn(ARETURN);
                visitor.visitMaxs(4, 5);
                visitor.visitEnd();
            }
        }
    }
    
    protected void writeBootstrapper(ClassWriter writer, Method method) {
        final MethodVisitor visitor;
        if (Modifier.isStatic(method.getModifiers())) {
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, "bootstrapInvoke", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
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
            visitor = writer.visitMethod(ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, "bootstrapInvoke", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;", null, null);
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
    //endregion
    
    protected String getExportedPackageFrom(Class<?> place) {
        final Module module = place.getModule();
        final String namespace;
        if (place.getPackageName().startsWith("java.lang")) namespace = "jdk.internal.reflect"; // prohibited namespace
        else namespace = place.getPackageName();
        if (getProvider() instanceof InternalAccessProvider provider)
            provider.export(module, namespace);
        return namespace;
    }
    
    interface Handles {
        
        static Handle getBootstrap(final Constructor<?> constructor) {
            try {
                if (Modifier.isPrivate(constructor.getModifiers())) {
                    return Handles.getHandle(Bootstrap.class.getMethod("bootstrapPrivateDynamicConstructor", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                } else {
                    return Handles.getHandle(Bootstrap.class.getMethod("bootstrapDynamicConstructor", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                }
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
        
        static Handle getBootstrap(final Method method) {
            try {
                if (Modifier.isPrivate(method.getModifiers())) {
                    if (Modifier.isStatic(method.getModifiers()))
                        return Handles.getHandle(Bootstrap.class.getMethod("bootstrapPrivate", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                    return Handles.getHandle(Bootstrap.class.getMethod("bootstrapPrivateDynamic", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                } else {
                    if (Modifier.isStatic(method.getModifiers()))
                        return Handles.getHandle(Bootstrap.class.getMethod("bootstrap", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                    return Handles.getHandle(Bootstrap.class.getMethod("bootstrapDynamic", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                }
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
        
        static Handle getBootstrap(final Field field, final boolean setter) {
            final boolean dynamic = !Modifier.isStatic(field.getModifiers());
            try {
                if (setter) {
                    if (dynamic)
                        return Handles.getHandle(Bootstrap.class.getMethod("bootstrapPrivateFieldSetter", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                    return Handles.getHandle(Bootstrap.class.getMethod("bootstrapPrivateStaticFieldSetter", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                } else {
                    if (dynamic)
                        return Handles.getHandle(Bootstrap.class.getMethod("bootstrapPrivateFieldGetter", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                    return Handles.getHandle(Bootstrap.class.getMethod("bootstrapPrivateStaticFieldGetter", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                }
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
        
        static Handle getHandle(final Method method) {
            final int code;
            if (Modifier.isStatic(method.getModifiers())) code = H_INVOKESTATIC;
            else if (Modifier.isAbstract(method.getModifiers())) code = H_INVOKEINTERFACE;
            else if (Modifier.isPrivate(method.getModifiers())) code = H_INVOKESPECIAL;
            else code = H_INVOKEVIRTUAL;
            return new Handle(code, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), code == H_INVOKEINTERFACE);
        }
        
        static Handle createHandle(String owner, String name, String descriptor) {
            return new Handle(H_INVOKESTATIC, owner, name, descriptor, false);
        }
        
    }
    
}
