package mx.kenzie.mirror;

import java.lang.invoke.*;

public final class Bootstrap {
    
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapPrivate(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller).findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapDynamic(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle;
        final MethodType end = type.dropParameterTypes(0, 1);
        handle = caller.findVirtual(owner, name, end);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapPrivateDynamic(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle;
        final MethodType end = type.dropParameterTypes(0, 1);
        handle = MethodHandles.privateLookupIn(owner, caller).findVirtual(owner, name, end);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapStaticFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapStaticFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapPrivateStaticFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapPrivateStaticFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapPrivateFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findVarHandle(owner, name, type.parameterType(1))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapPrivateFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapDynamicConstructor(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle;
        final MethodType end = type.changeReturnType(void.class);
        handle = caller.findConstructor(owner, end);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapPrivateDynamicConstructor(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle;
        final MethodType end = type.changeReturnType(void.class);
        handle = MethodHandles.privateLookupIn(owner, caller).findConstructor(owner, end);
        return new ConstantCallSite(handle);
    }
    
}
