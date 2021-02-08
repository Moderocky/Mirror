package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.MethodAccessorStub;
import org.junit.Test;

import java.lang.reflect.Method;

public class ExtendedFastMirrorSpeedTest {
    
    private final int revs = 10000;
    
    @Test
    public void test() throws Throwable {
        System.out.println("Traditional reflection via invocation: " + a1() + " nanos.");
        System.out.println("Special reflection via native accessor: " + a2() + " nanos.");
        assert a1() > a2();
    }
    
    public long a1() throws Throwable {
        final Sealed sealed = new Sealed();
        final Method method = Sealed.class.getDeclaredMethod("method");
        final long start = System.nanoTime(), end;
        for (int i = 0; i < revs; i++) {
            method.invoke(sealed);
        }
        end = System.nanoTime();
        return end - start;
    }
    
    public long a2() throws Throwable {
        final Sealed sealed = new Sealed();
        final Method method = Sealed.class.getDeclaredMethod("method");
        final MethodAccessorStub stub = Utilities.getNativeAccessor(method);
        final long start = System.nanoTime(), end;
        for (int i = 0; i < revs; i++) {
            stub.invoke(sealed);
        }
        end = System.nanoTime();
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
