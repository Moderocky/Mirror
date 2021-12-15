package mx.kenzie.mirror.test;

import mx.kenzie.mirror.ConstructorAccessor;
import mx.kenzie.mirror.FieldAccessor;
import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.junit.Test;

import java.io.PrintStream;
import java.lang.reflect.Field;

public class BasicTest {
    
    private String word = "Hello";
    
    public static void main(String[] args) throws Throwable {
        final Object object = new BasicTest();
        final int tries = 100000;
    
        warm_up: {
            for (int i = 0; i < 1000; i++) {
                final Mirror<?> mirror = Mirror.of(object);
                final FieldAccessor<String> field = mirror.field("word");
                assert field.get().equals("Hello");
                field.set("Goodbye");
                assert field.get().equals("Goodbye");
            }
        }
        
        Mirror<?> mirror = Mirror.of(object);
        FieldAccessor<String> field = mirror.field("word");
        make_mirror: {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                mirror = Mirror.of(object);
            }
            end = System.nanoTime();
            System.out.println("Making mirror " + tries + " times took " + (end-start) + " nanos. (Avg. " + (end-start)/tries + ")");
        }
        make_accessor: {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field = mirror.field("word");
            }
            end = System.nanoTime();
            System.out.println("Making accessor " + tries + " times took " + (end-start) + " nanos. (Avg. " + (end-start)/tries + ")");
        }
        set_accessor: {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.set("Goodbye");
            }
            end = System.nanoTime();
            System.out.println("Setting field " + tries + " times took " + (end-start) + " nanos. (Avg. " + (end-start)/tries + ")");
        }
        get_accessor: {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                field.get();
            }
            end = System.nanoTime();
            System.out.println("Getting field " + tries + " times took " + (end-start) + " nanos. (Avg. " + (end-start)/tries + ")");
        }
    
        Field obj = BasicTest.class.getDeclaredField("word");
        final BasicTest test = new BasicTest();
    
        set_accessor: {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                obj.set(test, "Goodbye");
            }
            end = System.nanoTime();
            System.out.println("Setting reflection field " + tries + " times took " + (end-start) + " nanos. (Avg. " + (end-start)/tries + ")");
        }
        get_accessor: {
            final long start, end;
            start = System.nanoTime();
            for (int i = 0; i < tries; i++) {
                obj.get(test);
            }
            end = System.nanoTime();
            System.out.println("Getting reflection field " + tries + " times took " + (end-start) + " nanos. (Avg. " + (end-start)/tries + ")");
        }
        
    }
    
    @Test
    public void test() {
        final Mirror<?> mirror = Mirror.of(this);
        final FieldAccessor<String> field = mirror.field("word");
        assert field.get().equals("Hello");
        field.set("Goodbye");
        final String string = field.get();
        assert string.equals("Goodbye");
    }
    
    @Test
    public void examples() {
        final MethodAccessor<?> method = Mirror.of(System.out).method("println", String.class);
        method.invoke("hello");
        
        Mirror.of(System.out)
            .method("println", String.class)
            .invoke("hello");
    
        long value = (long) Mirror.of(System.class)
            .method("nanoTime")
            .invoke();
        assert value > -1;
        
        final FieldAccessor<PrintStream> field = Mirror.of(System.class).field("out");
        field.get().println("hello");
        
        Mirror.of(System.class)
            .field("out")
            .mirror()
            .method("println", int.class)
            .invoke(2);
        
        Mirror.of(this)
            .field("word")
            .set("bean");
        
        Mirror.of(ConstructorAccessorTest.TestConstructor.class)
            .constructor(int.class, int.class)
            .newInstance(0, 0);
        
    }
    
}
