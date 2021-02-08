package mx.kenzie.mirror;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;

public class InstanceAllocationTest {
    
    @Before
    public void speedUp() { // For more accurate test speeds
        Utilities.createInstance(String.class);
        Utilities.createInstance(String.class, "hoi");
        Utilities.createInstance(Object.class);
        Utilities.allocateInstance(String.class);
        try {
            Utilities.allocateInstance(String.class, null);
        } catch (Throwable ignored) { }
    }
    
    @Test
    public void nullaryConstruction() {
        final Object object = Utilities.createInstance(Object.class);
        assert object != null;
    }
    
    @Test
    public void unaryConstruction() {
        final String string = Utilities.createInstance(String.class, "hoi");
        assert string != null;
        assert string.equals("hoi");
    }
    
    @Test
    public void zeroArrayAllocation() {
        final String[] strings = Utilities.createInstance(String[].class);
        assert strings != null;
        assert strings.length == 0;
    }
    
    @Test
    public void sizedArrayAllocation() {
        final String[] strings = Utilities.createInstance(String[].class, 6);
        assert strings != null;
        assert strings.length == 6;
    }
    
    @Test
    public void instanceAllocation() {
        final Blob blob = Utilities.createInstance(Blob.class);
        assert blob != null;
        assert blob.string == null;
        assert blob.number == 0;
    }
    
    @Test
    public void replacementConstructor() throws Throwable {
        final Constructor<Boo> constructor = Boo.class.getConstructor();
        final Blob blob = Utilities.createInstance(Blob.class, constructor);
        assert blob != null;
        assert blob.string == null;
        assert blob.number == 4;
    }
    
    @Test
    public void replacementConstructorWithArgs() throws Throwable {
        final Constructor<Boo> constructor = Boo.class.getConstructor(int.class);
        final Blob blob = Utilities.createInstance(Blob.class, constructor, 6);
        assert blob != null;
        assert blob.string == null;
        assert blob.number == 6;
    }
    
    private static class Blob {
        
        public final String string;
        public final int number;
        
        protected Blob(String string) {
            this.string = string;
            this.number = 3;
        }
        
    }
    
    private static class Boo {
        public final int number;
    
        public Boo() {
            this.number = 4;
        }
    
        public Boo(int i) {
            this.number = i;
        }
        
    }
    
}
