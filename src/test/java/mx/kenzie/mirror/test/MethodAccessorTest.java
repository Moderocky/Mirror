package mx.kenzie.mirror.test;

import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodAccessorTest {
    
    private static int foo(int i) {
        return i+1;
    }
    
    private int bar(int i) {
        return i+2;
    }
    
    public static int foo(int i, int j) {
        return i+j;
    }
    
    public int bar(int i, int j) {
        return i+j;
    }
    
    @BeforeClass
    public static void warmUp() {
        final Mirror<?> mirror = Mirror.of(new MethodAccessorTest());
        mirror.method("foo", int.class).invoke(0);
        mirror.method("foo", int.class, int.class).invoke(0, 0);
        mirror.method("bar", int.class).invoke(0);
        mirror.method("bar", int.class, int.class).invoke(0, 0);
    }
    
    @Test
    public void privateDynamic() {
        final MethodAccessor<?> accessor = Mirror.of(this).method("bar", int.class);
        assert accessor != null;
        assert !accessor.isStatic();
        assert accessor.isDynamicAccess();
        assert (int) accessor.invoke(2) == 4;
    }
    
    @Test
    public void privateStatic() {
        final MethodAccessor<?> accessor = Mirror.of(MethodAccessorTest.class).method("foo", int.class);
        assert accessor != null;
        assert accessor.isStatic();
        assert accessor.isDynamicAccess();
        assert (int) accessor.invoke(2) == 3;
    }
    
    @Test
    public void publicDynamic() {
        final MethodAccessor<?> accessor = Mirror.of(this).method("bar", int.class, int.class);
        assert accessor != null;
        assert !accessor.isStatic();
        assert !accessor.isDynamicAccess();
        assert (int) accessor.invoke(2, 6) == 8;
    }
    
    @Test
    public void publicStatic() {
        final MethodAccessor<?> accessor = Mirror.of(this).method("foo", int.class, int.class);
        assert accessor != null;
        assert accessor.isStatic();
        assert !accessor.isDynamicAccess();
        assert (int) accessor.invoke(2, 3) == 5;
    }
    
}
