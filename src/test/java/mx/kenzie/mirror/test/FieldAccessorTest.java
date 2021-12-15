package mx.kenzie.mirror.test;

import mx.kenzie.mirror.FieldAccessor;
import mx.kenzie.mirror.Mirror;
import org.junit.BeforeClass;
import org.junit.Test;

public class FieldAccessorTest {
    
    public int a;
    public static int b;
    private int c;
    private static int d;
    
    @BeforeClass
    public static void warmUp() {
        final Mirror<?> mirror = Mirror.of(new FieldAccessorTest());
        mirror.field("a").get();
        mirror.field("b").get();
        mirror.field("c").get();
        mirror.field("d").get();
    }
    
    @Test
    public void publicDynamic() {
        final FieldAccessor<?> accessor = Mirror.of(this).field("a");
        assert accessor != null;
        assert !accessor.isStatic();
        assert !accessor.isDynamicAccess();
        accessor.set(3);
        assert (int) accessor.get() == 3;
    }
    
    @Test
    public void publicStatic() {
        final FieldAccessor<?> accessor = Mirror.of(this).field("b");
        assert accessor != null;
        assert accessor.isStatic();
        assert !accessor.isDynamicAccess();
        accessor.set(66);
        assert (int) accessor.get() == 66;
    }
    
    @Test
    public void privateDynamic() {
        final FieldAccessor<?> accessor = Mirror.of(this).field("c");
        assert accessor != null;
        assert !accessor.isStatic();
        assert accessor.isDynamicAccess();
        accessor.set(14);
        assert (int) accessor.get() == 14;
    }
    
    @Test
    public void privateStatic() {
        final FieldAccessor<?> accessor = Mirror.of(this).field("d");
        assert accessor != null;
        assert accessor.isStatic();
        assert accessor.isDynamicAccess();
        accessor.set(22);
        assert (int) accessor.get() == 22;
    }
    
}
