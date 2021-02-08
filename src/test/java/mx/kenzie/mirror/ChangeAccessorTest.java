package mx.kenzie.mirror;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ChangeAccessorTest {
    
    @Test
    public void test() throws Throwable {
        assert foo() == 1;
        assert bar() == 2;
        final Method method = ChangeAccessorTest.class.getDeclaredMethod("foo");
        final Method alternative = ChangeAccessorTest.class.getDeclaredMethod("bar");
        final Field delegate = InternalAccessor.delegatingMethodAccessorImplClass.getDeclaredField("delegate");
        assert delegate != null;
        assert method != null;
        assert alternative != null;
        Utilities.setAccessible(delegate, true);
        Object accessor = InternalAccessor.getMethodAccessor.invoke(method);
        if (accessor == null) accessor = InternalAccessor.acquireMethodAccessor.invoke(method);
        assert accessor != null;
        assert InternalAccessor.delegatingMethodAccessorImplClass.isInstance(accessor);
        final int real = (int) Utilities.invoke(method, accessor, this);
        assert real == 1;
        assert foo() == 1;
        Object successor = InternalAccessor.getMethodAccessor.invoke(alternative);
        if (successor == null) successor = InternalAccessor.acquireMethodAccessor.invoke(alternative);
        assert successor != null;
        assert InternalAccessor.delegatingMethodAccessorImplClass.isInstance(successor);
        final Object nativeA = delegate.get(accessor);
        final Object nativeB = delegate.get(successor);
        assert nativeA != null;
        assert nativeB != null;
        assert nativeA != nativeB;
        delegate.set(accessor, nativeB);
        assert delegate.get(accessor) == nativeB;
        final int fake = (int) Utilities.invoke(method, accessor, this);
        assert fake != 1;
        assert fake == 2;
        assert foo() == 1;
        assert (int) method.invoke(this) == 2;
    }
    
    public int foo() {
        return 1;
    }
    
    public int bar() {
        return 2;
    }
    
    public void reflected$foo() {
        return;
    }

}
