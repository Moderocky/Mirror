package mx.kenzie.mirror;

import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;


@Fork(3)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class SetupOperationBenchmark {

    @Benchmark
    public void varHandleGet() throws Throwable {
        final Lookup lookup = MethodHandles.lookup();
        final VarHandle handle = lookup.findStaticVarHandle(Foo.class, "string", String.class);
        handle.set("hi");
    }

    @Benchmark
    public void varHandleSet() throws Throwable {
        final Lookup lookup = MethodHandles.lookup();
        final VarHandle handle = lookup.findStaticVarHandle(Foo.class, "string", String.class);
        handle.get();
    }

    @Benchmark
    public void reflectionGet() throws Throwable {
        final Field field = Foo.class.getDeclaredField("string");
        field.setAccessible(true);
        field.get(null);
    }

    @Benchmark
    public void reflectionSet() throws Throwable {
        final Field field = Foo.class.getDeclaredField("string");
        field.setAccessible(true);
        field.set(null, "hi");
    }

    @Benchmark
    public void basicMirrorGet() {
        Mirror.ofClass(Foo.class).field("string").get();
    }

    @Benchmark
    public void basicMirrorSet() {
        Mirror.ofClass(Foo.class).field("string").set("hi");
    }
//
//     Trying to benchmark this will crash the VM =)
//
//    @Benchmark
//    public void fastMirrorGet() {
//        Mirror.ofClass(Foo.class).fastField("string").get();
//    }
//
//    @Benchmark
//    public void fastMirrorSet() {
//        Mirror.ofClass(Foo.class).fastField("string").set("hi");
//    }
    
    private static class Foo {
        
        private static String string = "hello";
        
    }
    
    public static void main(String... args) throws Throwable {
        Main.main(args);
    }
    
}
