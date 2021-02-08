package mx.kenzie.mirror;

import org.junit.Test;

import java.lang.reflect.Method;

public class MirrorSpeedTest {
    
    @Test
    public void test() throws Throwable {
//        System.out.println("Traditional reflection via invocation: " + a1() + " nanos.");
//        System.out.println("Special reflection via native accessor: " + a2() + " nanos.");
        assert a1() > a2();
    }
    
    public long a1() throws Throwable {
        final Sealed sealed = new Sealed();
        final long start = System.nanoTime(), end;
        final Method method = Sealed.class.getDeclaredMethod("method");
        final int number = (int) method.invoke(sealed);
        end = System.nanoTime();
        assert number == 1;
        return end - start;
    }
    
    public long a2() {
        final Sealed sealed = new Sealed();
        Unsealed unsealed = new Mirror<>(sealed).magic(Unsealed.class);
        assert unsealed.method() > 0;
        final long start = System.nanoTime(), end;
        final int number = unsealed.method();
        end = System.nanoTime();
        assert number == 1;
        return end - start;
    }
    
    private static class Sealed {
        
        private int method() {
            return 1;
        }
        
    }
    
    public interface Unsealed {
        
        int method();
        
    }
    
}
