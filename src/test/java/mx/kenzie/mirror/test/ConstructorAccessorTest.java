package mx.kenzie.mirror.test;

import mx.kenzie.mirror.ConstructorAccessor;
import mx.kenzie.mirror.Mirror;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class ConstructorAccessorTest {
    
    public static class TestConstructor {
    
        public TestConstructor(int a, int b) {
        
        }
    
        private TestConstructor(int a, String b) {
        
        }
        
    }
    
    @BeforeClass
    public static void warmUp() {
        final Mirror<?> mirror = Mirror.of(TestConstructor.class);
        mirror.constructor(int.class, String.class).invoke(0, null);
        mirror.constructor(int.class, int.class).invoke(0, 1);
    }
    
    @Test
    public void dynamic() {
        final ConstructorAccessor<?> constructor = Mirror.of(TestConstructor.class).constructor(int.class, String.class);
        final Object object = constructor.newInstance(1, "hi");
        assert object instanceof TestConstructor;
    }
    
    @Test
    public void normal() {
        final ConstructorAccessor<?> constructor = Mirror.of(TestConstructor.class).constructor(int.class, int.class);
        final Object object = constructor.newInstance(1, 2);
        assert object instanceof TestConstructor;
    }
    
}
