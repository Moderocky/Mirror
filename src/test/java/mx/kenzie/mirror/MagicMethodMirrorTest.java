package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.Reflected;
import org.junit.Test;

public class MagicMethodMirrorTest {
    
    @Test
    public void test() {
        final Sealed sealed = new Sealed();
        final Unsealed unsealed = new Mirror<>(sealed).magic(Unsealed.class);
        assert unsealed.method() > 0;
        final long start = System.nanoTime();
        final int number = unsealed.method();
        assert (System.nanoTime() - start) < 20000;
        assert number == 1;
    }
    
    @Test
    public void local() {
        final Sealed sealed = new Sealed();
        interface Thing {
            int method();
            String blob();
            Object getOriginal();
            Class<?> getOriginalClass();
        }
        final Thing unsealed = new Mirror<>(sealed).magic(Thing.class);
        assert unsealed.method() == 1;
        assert unsealed.blob() == null;
        assert unsealed.getOriginalClass() == Sealed.class;
        assert unsealed.getOriginal() == sealed;
        final long start = System.nanoTime();
        final int number = unsealed.method();
        assert (System.nanoTime() - start) < 20000;
        assert number == 1;
    }
    
    @Test
    public <
        Generic extends Unsealed & Reflected<Sealed>
        > void generic() {
        final Sealed sealed = new Sealed();
        final Generic unsealed = new Mirror<>(sealed).magic(Unsealed.class, Reflected.class);
        assert unsealed.method() == 1;
        assert unsealed.getOriginalClass() == Sealed.class;
        assert unsealed.getOriginal() == sealed;
        final long start = System.nanoTime();
        final int number = unsealed.method();
        assert (System.nanoTime() - start) < 20000;
        assert number == 1;
    }
    
    @Test
    public void annotation() {
        final Sealed sealed = new Sealed();
        final Access unsealed = new Mirror<>(sealed).magic(Access.class);
        assert unsealed.method() == 1;
        assert unsealed.blob() == null;
        assert unsealed.getOriginalClass() == Sealed.class;
        final long start = System.nanoTime();
        final int number = unsealed.method();
        assert (System.nanoTime() - start) < 20000;
        assert number == 1;
    }
    
    private static class Sealed {
        private int method() {
            return 1;
        }
    }
    
    interface Unsealed {
        int method();
    }
    
    @interface Access {
        int method();
        String blob();
        Class<?> getOriginalClass();
    }
    
}
