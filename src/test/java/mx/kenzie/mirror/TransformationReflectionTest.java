package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.FieldAccessorStub;
import mx.kenzie.mirror.copy.MethodAccessorStub;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TransformationReflectionTest {
    
    private FieldAccessorStub stub;
    
    @Test
    public void ofMethod() throws Throwable {
        final Method method = Foo.class.getDeclaredMethod("blob");
        assert method != null;
        final MethodAccessorStub accessor = Utilities.makeBytecodeAccessor(method);
        assert accessor != null;
        assert (int) accessor.invoke(new Foo(), new Object[0]) == 1;
    }
    
    @Test
    public void ofField() throws Throwable {
        final Foo foo = new Foo();
        final Field field = Foo.class.getDeclaredField("number");
        assert field != null;
        final FieldAccessorStub accessor = Utilities.getNativeAccessor(field);
        assert accessor != null;
        assert accessor.getInt(foo) == 6;
        accessor.setInt(foo, 7);
        assert accessor.getInt(foo) == 7;
        assert field.getInt(foo) == 7;
        assert foo.number == 7;
    }
    
    @Test
    public void stored() throws Throwable {
        final Field field = Foo.class.getDeclaredField("number");
        assert field != null;
        final FieldAccessorStub accessor = Utilities.getNativeAccessor(field);
        this.stub = accessor;
        assert accessor != null;
        assert accessor.getInt(new Foo()) == 6;
        assert stub.getInt(new Foo()) == 6;
    }

    @Test
    public void methodSpeedTest() throws Throwable {
        a1();
        a2();
        a3();
        System.out.println("Method access via reflection invocation: " + a1() + " nanos.");
        System.out.println("Method access via bytecode accessor: " + a2() + " nanos.");
        System.out.println("Method access via transformed accessor: " + a3() + " nanos.");
        assert a1() > a2();
        assert a2() > a3();
    }
    
    @Test
    public void fieldSpeedTest() throws Throwable {
        b1();
        b2();
        System.out.println("Field access via invocation: " + b1() + " nanos.");
        System.out.println("Field access via transformed accessor: " + b2() + " nanos.");
        assert b1() > b2();
    }
    
    public long b1() throws Throwable {
        final Foo foo = new Foo();
        final Field field = Foo.class.getDeclaredField("number");
        final long start = System.nanoTime(), end;
        final int number = field.getInt(foo);
        end = System.nanoTime();
        assert number == 6;
        return end - start;
    }
    
    public long b2() throws Throwable {
        final Foo foo = new Foo();
        final Field field = Foo.class.getDeclaredField("number");
        final FieldAccessorStub accessor = Utilities.getNativeAccessor(field);
        final long start = System.nanoTime(), end;
        final int number = accessor.getInt(foo);
        end = System.nanoTime();
        assert number == 6;
        return end - start;
    }

    public long a1() throws Throwable {
        final Foo foo = new Foo();
        final Method method = Foo.class.getDeclaredMethod("blob");
        final long start = System.nanoTime(), end;
        final int number = (int) method.invoke(foo);
        end = System.nanoTime();
        assert number == 1;
        return end - start;
    }

    public long a2() throws Throwable {
        final Foo foo = new Foo();
        final Method method = Foo.class.getDeclaredMethod("blob");
        final MethodAccessorStub accessor = Utilities.makeBytecodeAccessor(method);
        final long start = System.nanoTime(), end;
        final int number = (int) accessor.invoke(foo);
        end = System.nanoTime();
        assert number == 1;
        return end - start;
    }
    
    public long a3() throws Throwable {
        final Foo foo = new Foo();
        final Method method = Foo.class.getDeclaredMethod("blob");
        final MethodAccessorStub accessor = Utilities.getNativeAccessor(method);
        final long start = System.nanoTime(), end;
        final int number = (int) accessor.invoke(foo);
        end = System.nanoTime();
        assert number == 1;
        return end - start;
    }
    
    private static class Foo {
        
        private final int number;
        
        public Foo() {
            this.number = 6;
        }
        
        private int blob() {
            return 1;
        }
        
    }
    
}
