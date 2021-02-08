package mx.kenzie.mirror;

import org.junit.Test;

import java.lang.reflect.Method;

public class MakeAccessorTest {
    
    @Test
    public void test() throws Throwable {
        final Method method = this.getClass().getDeclaredMethod("blob");
        assert method != null;
        final Object accessor = Utilities.getExistingAccessor(method);
        assert accessor != null;
        final Object result = Utilities.invoke(method, accessor, this);
        assert result.equals("hello");
    }
    
    public void speedTest() throws Throwable {
        final Method method = this.getClass().getDeclaredMethod("blob");
        assert method != null;
        {
            final Object accessor = Utilities.getExistingAccessor(method);
            assert accessor != null;
            for (int i = 0; i < 1000000; i++) {
                final Object result = method.invoke(this);
                assert result.equals("hello");
            }
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < 1000000; i++) {
                final Object result = method.invoke(this);
                assert result.equals("hello");
            }
            end = System.nanoTime();
            System.out.println("Normal: " + (end - start));
        }
        {
            final Object accessor = Utilities.getExistingAccessor(method);
            assert accessor != null;
            for (int i = 0; i < 1000000; i++) {
                final Object result = Utilities.invokeNative(method, accessor, this);
                assert result.equals("hello");
            }
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < 1000000; i++) {
                final Object result = Utilities.invokeNative(method, accessor, this);
                assert result.equals("hello");
            }
            end = System.nanoTime();
            System.out.println("Kenzie: " + (end - start));
        }
        {
            final Object accessor = Utilities.makeFastAccessor(method);
            assert accessor != null;
            for (int i = 0; i < 1000000; i++) {
                final Object result = Utilities.invokeNative(method, accessor, this);
                assert result.equals("hello");
            }
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < 1000000; i++) {
                final Object result = Utilities.invokeNative(method, accessor, this);
                assert result.equals("hello");
            }
            end = System.nanoTime();
            System.out.println("Kenzie: " + (end - start));
        }
        
    }
    
    private Object blob() {
        return "hello";
    }
    
}
