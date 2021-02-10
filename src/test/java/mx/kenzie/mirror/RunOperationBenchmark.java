package mx.kenzie.mirror;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;


@Fork(1)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class RunOperationBenchmark {
    
    private static VarHandle handle;
    private static Field field;
    private static FieldMirror<String> basic;
    private static FieldMirror<String> fast;
    
    static {
        try {
            handle = MethodHandles.lookup().findStaticVarHandle(Foo.class, "string", String.class);
            field = Foo.class.getDeclaredField("string");
            field.setAccessible(true);
            basic = Mirror.ofClass(Foo.class).field("string");
            fast = Mirror.ofClass(Foo.class).fastField("string");
        } catch (Throwable ex) {}
    }
    
    public static void main(String... args) throws Throwable {
        Main.main(args);
    }
    
    @Benchmark
    public void varHandle() throws Throwable {
        assert handle.get().equals("hello");
    }
    
    @Benchmark
    public void varHandleSet() throws Throwable {
        handle.set("hello");
    }
    
    @Benchmark
    public void reflectionGet() throws Throwable {
        assert field.get(null).equals("hello");
    }
    
    @Benchmark
    public void reflectionSet() throws Throwable {
        field.set(null, "hello");
    }
    
    @Benchmark
    public void basicMirrorGet() {
        assert basic.get().equals("hello");
    }
    
    @Benchmark
    public void basicMirrorSet() {
        basic.set("hello");
    }
    
    @Benchmark
    public void fastMirrorGet() {
        assert fast.get().equals("hello");
    }
    
    @Benchmark
    public void fastMirrorSet() {
        fast.set("hello");
    }
    
    private static class Foo {
        
        private static final String string = "hello";
        
    }
    
}
