package mx.kenzie.mirror.test;

import mx.kenzie.mirror.Accessor;
import mx.kenzie.mirror.FieldAccessor;
import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StandardBenchmarks {
    
    static int tries = 100000;
    static final TestMirror READER = new TestMirror(Object.class);
    
    public static class TestMirror extends Mirror<Object> {
        public TestMirror(Object target) {
            super(target);
        }
        
        @Override
        public byte[] retrieveCode(Accessor object) {
            return super.retrieveCode(object);
        }
    }
    
    public static class Thing {
        
        public int a = 1;
        @SuppressWarnings("all")
        private int b = 2;
        public static int c = 3;
        @SuppressWarnings("all")
        private static int d = 4;
        
        public int a() {
            return 1;
        }
        
        private int b() {
            return 2;
        }
        
        public static int c() {
            return 3;
        }
        
        private static int d() {
            return 4;
        }
        
    }
    
    public interface Intrinsic {
        int $a();
        
        void $a(int i);
        
        int $b();
        
        void $b(int i);
        
        int $c();
        
        void $c(int i);
        
        int $d();
        
        void $d(int i);
        
        int a();
        
        int b();
        
        int c();
        
        int d();
    }
    
    public static void main(String[] args) throws Throwable {
        new StandardBenchmarks().speedTest();
    }
    
    public void speedTest() throws Throwable {
        if (fieldGetPublicDynamic() != 3) throw new Error("Failed.");
        if (fieldGetPrivateDynamic() != 3) throw new Error("Failed.");
        if (fieldGetPublicStatic() != 3) throw new Error("Failed.");
        if (fieldGetPrivateStatic() != 3) throw new Error("Failed.");
        
        if (fieldSetPublicDynamic() != 3) throw new Error("Failed.");
        if (fieldSetPrivateDynamic() != 3) throw new Error("Failed.");
        if (fieldSetPublicStatic() != 3) throw new Error("Failed.");
        if (fieldSetPrivateStatic() != 3) throw new Error("Failed.");
        
        if (methodPublicDynamic() != 3) throw new Error("Failed.");
        if (methodPrivateDynamic() != 3) throw new Error("Failed.");
        if (methodPublicStatic() != 3) throw new Error("Failed.");
        if (methodPrivateStatic() != 3) throw new Error("Failed.");
    }
    
    //region Field Getters
    public static int fieldGetPublicDynamic() throws Throwable {
        System.out.println();
        System.out.println("Accessing public dynamic field " + tries + " times.");
        
        final int value = 1;
        final Thing object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("a");
        final Field field = Thing.class.getDeclaredField("a");
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.get() == value;
                assert (int) field.get(object) == value;
                assert (int) handle.get(object) == value;
                assert intrinsic.$a() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.get(object);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.get(object);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.get();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        normal:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                int j = object.a;
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Normal access took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$a();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int fieldGetPrivateDynamic() throws Throwable {
        System.out.println();
        System.out.println("Accessing private dynamic field " + tries + " times.");
        
        final int value = 2;
        final Object object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("b");
        final Field field = Thing.class.getDeclaredField("b");
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.get() == value;
                assert (int) field.get(object) == value;
                assert (int) handle.get(object) == value;
                assert intrinsic.$b() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.get(object);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.get(object);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.get();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$b();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int fieldGetPublicStatic() throws Throwable {
        System.out.println();
        System.out.println("Accessing public static field " + tries + " times.");
        
        final int value = 3;
        final Thing object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("c");
        final Field field = Thing.class.getDeclaredField("c");
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.get() == value;
                assert (int) field.get(object) == value;
                assert (int) handle.get(object) == value;
                assert intrinsic.$c() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.get(null);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.get();
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.get();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$c();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        normal:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                int j = Thing.c;
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Normal access took: " + result + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("Mirror took: " + mirror + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int fieldGetPrivateStatic() throws Throwable {
        System.out.println();
        System.out.println("Accessing private static field " + tries + " times.");
        
        final int value = 4;
        final Object object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("d");
        final Field field = Thing.class.getDeclaredField("d");
        field.setAccessible(true);
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.get() == value;
                assert (int) field.get(object) == value;
                assert (int) handle.get(object) == value;
                assert intrinsic.$d() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.get(null);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.get();
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.get();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$d();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    //endregion
    
    //region Field Setters
    public static int fieldSetPublicDynamic() throws Throwable {
        System.out.println();
        System.out.println("Setting public dynamic field " + tries + " times.");
        
        final int value = 1;
        final Thing object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("a");
        final Field field = Thing.class.getDeclaredField("a");
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        assert !accessor.isDynamicAccess() && !accessor.isStatic();
        for (int i = 0; i < tries; i++)
            warm_up:{
                accessor.set(value);
                field.set(object, value);
                handle.set(object, value);
                intrinsic.$a(value);
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.set(object, value);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.set(object, value);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.set(value);
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$a(value);
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        normal:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                object.a = value;
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Normal access took: " + result + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int fieldSetPrivateDynamic() throws Throwable {
        System.out.println();
        System.out.println("Setting private dynamic field " + tries + " times.");
        
        final int value = 2;
        final Object object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("b");
        final Field field = Thing.class.getDeclaredField("b");
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        assert accessor.isDynamicAccess() && !accessor.isStatic();
        field.setAccessible(true);
        for (int i = 0; i < tries; i++)
            warm_up:{
                accessor.set(value);
                field.set(object, value);
                handle.set(object, value);
                intrinsic.$b(value);
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.set(object, value);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.set(object, value);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.set(value);
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$a(value);
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int fieldSetPublicStatic() throws Throwable {
        System.out.println();
        System.out.println("Setting public static field " + tries + " times.");
        
        final int value = 3;
        final Object object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("c");
        final Field field = Thing.class.getDeclaredField("c");
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        assert !accessor.isDynamicAccess() && accessor.isStatic();
        for (int i = 0; i < tries; i++)
            warm_up:{
                accessor.set(value);
                field.set(null, value);
                handle.set(value);
                intrinsic.$c(value);
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.set(null, value);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.set(value);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.set(value);
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$c(value);
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        normal:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                Thing.c = value;
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Normal access took: " + result + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int fieldSetPrivateStatic() throws Throwable {
        System.out.println();
        System.out.println("Setting private static field " + tries + " times.");
        
        final int value = 4;
        final Object object = new Thing();
        final FieldAccessor<Integer> accessor = Mirror.of(object).field("d");
        final Field field = Thing.class.getDeclaredField("d");
        final VarHandle handle = MethodHandles.lookup().unreflectVarHandle(field);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        assert accessor.isDynamicAccess() && accessor.isStatic();
        field.setAccessible(true);
        for (int i = 0; i < tries; i++)
            warm_up:{
                accessor.set(value);
                field.set(null, value);
                handle.set(value);
                intrinsic.$d(value);
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.set(null, value);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.set(value);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.set(value);
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.$d(value);
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    //endregion
    
    //region Method Invokers
    public static int methodPublicDynamic() throws Throwable {
        System.out.println();
        System.out.println("Invoking public dynamic method " + tries + " times.");
        
        final int value = 1;
        final Object object = new Thing();
        final MethodAccessor<Integer> accessor = Mirror.of(object).method("a");
        final Method method = Thing.class.getDeclaredMethod("a");
        final MethodHandle handle = MethodHandles.lookup().unreflect(method);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.invoke() == value;
                assert (int) method.invoke(object) == value;
                assert (int) handle.invoke(object) == value;
                assert intrinsic.a() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                method.invoke(object);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.invoke(object);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.invoke();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.a();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int methodPrivateDynamic() throws Throwable {
        System.out.println();
        System.out.println("Invoking private dynamic method " + tries + " times.");
        
        final int value = 2;
        final Object object = new Thing();
        final MethodAccessor<Integer> accessor = Mirror.of(object).method("b");
        final Method method = Thing.class.getDeclaredMethod("b");
        final MethodHandle handle = MethodHandles.lookup().unreflect(method);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.invoke() == value;
                assert (int) method.invoke(object) == value;
                assert (int) handle.invoke(object) == value;
                assert intrinsic.b() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                method.invoke(object);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.invoke(object);
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.invoke();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.b();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int methodPublicStatic() throws Throwable {
        System.out.println();
        System.out.println("Invoking public static method " + tries + " times.");
        
        final int value = 3;
        final Object object = new Thing();
        final MethodAccessor<Integer> accessor = Mirror.of(object).method("c");
        final Method method = Thing.class.getDeclaredMethod("c");
        final MethodHandle handle = MethodHandles.lookup().unreflect(method);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.invoke() == value;
                assert (int) method.invoke(null) == value;
                assert (int) handle.invoke() == value;
                assert intrinsic.c() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                method.invoke(null);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.invoke();
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.invoke();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.a();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    
    public static int methodPrivateStatic() throws Throwable {
        System.out.println();
        System.out.println("Invoking private static method " + tries + " times.");
        
        final int value = 4;
        final Object object = new Thing();
        final MethodAccessor<Integer> accessor = Mirror.of(object).method("d");
        final Method method = Thing.class.getDeclaredMethod("d");
        final MethodHandle handle = MethodHandles.lookup().unreflect(method);
        final Intrinsic intrinsic = Mirror.of(object).magicIntrinsic(Intrinsic.class);
        for (int i = 0; i < tries; i++)
            warm_up:{
                assert accessor.invoke() == value;
                assert (int) method.invoke(null) == value;
                assert (int) handle.invoke() == value;
                assert intrinsic.d() == value;
            }
        final long mirror;
        final long handles;
        final long reflect;
        
        reflection:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                method.invoke(null);
            }
            end = System.nanoTime();
            reflect = (end - start) / tries;
        }
        
        handle:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                handle.invoke();
            }
            end = System.nanoTime();
            handles = (end - start) / tries;
        }
        
        mirror:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                accessor.invoke();
            }
            end = System.nanoTime();
            final long result = (end - start) / tries;
            System.out.println("Mirror accessor took: " + result + " nanos.");
        }
        
        intrinsic:
        {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                intrinsic.b();
            }
            end = System.nanoTime();
            mirror = (end - start) / tries;
            System.out.println("Intrinsic magic mirror took: " + mirror + " nanos.");
        }
        
        System.out.println("Java reflection took: " + reflect + " nanos.");
        System.out.println("Java MethodHandles took: " + handles + " nanos.");
        System.out.println("The winner was: " + (reflect > handles && mirror > handles ? "Handles" : mirror > reflect ? "Reflection" : "Mirror"));
        return (reflect > handles && mirror > handles ? 2 : mirror > reflect ? 1 : 3);
    }
    //endregion
    
    
}
