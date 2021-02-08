package mx.kenzie.mirror;

import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class KeyValueMatcherSpeedTest {
    
    final int revs = 1000;
    
    @Test
    public void test() throws Throwable {
        assert a1() > a2();
    }
    
    public long a1() throws Throwable {
        final Method[] real = TestClass.class.getDeclaredMethods();
        final Method[] indices = OtherClass.class.getDeclaredMethods();
        final Method method = OtherClass.class.getDeclaredMethod("i");
        final long start = System.nanoTime(), end;
        for (int i = 0; i < revs; i++) {
            Method found = real[indexOf(indices, method)];
            assert found != null;
        }
        end = System.nanoTime();
        return end - start;
    }
    
    public long a2() throws Throwable {
        final Method[] real = TestClass.class.getDeclaredMethods();
        final Method[] indices = OtherClass.class.getDeclaredMethods();
        final Map<Method, Method> map = new HashMap<>();
        for (int i = 0; i < real.length; i++) {
            map.put(indices[i], real[i]);
        }
        final Method method = OtherClass.class.getDeclaredMethod("i");
        final long start = System.nanoTime(), end;
        for (int i = 0; i < revs; i++) {
            Method found = map.get(method);
            assert found != null;
        }
        end = System.nanoTime();
        return end - start;
    }
    
    private int indexOf(Object[] objects, Object thing) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].equals(thing)) return i;
        }
        return -1;
    }
    
    private static class TestClass {
        
        public native void a();
        public native void b();
        public native void c();
        public native void d();
        public native void e();
        public native void f();
        public native void g();
        public native void h();
        public native void i();
        public native void j();
        
    }
    
    private static class OtherClass {
        
        public native void a();
        public native void b();
        public native void c();
        public native void d();
        public native void e();
        public native void f();
        public native void g();
        public native void h();
        public native void i();
        public native void j();
        
    }
    
    
}
