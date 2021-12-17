package mx.kenzie.mirror.test;

import mx.kenzie.mirror.FieldAccessor;
import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.junit.Test;

import java.io.PrintStream;

public class BasicTest {
    
    @SuppressWarnings("all")
    private String word = "Hello";
    
    private int blob() {
        return 6;
    }
    
    public int blob2() {
        return 5;
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
        
        interface Test {
            int blob();
        }
        final Test test = Mirror.of(this)
            .magic(Test.class);
        assert test.blob() == 6;
        
    }
    
}
