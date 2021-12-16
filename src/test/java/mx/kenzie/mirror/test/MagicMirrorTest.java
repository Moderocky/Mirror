package mx.kenzie.mirror.test;

import mx.kenzie.mirror.Mirror;
import org.junit.Test;

public class MagicMirrorTest {
    
    private int blob() {
        return 6;
    }
    
    private static int bean(int i) {
        return i + 2;
    }
    
    public String name() {
        return "Hello";
    }
    
    @Test
    public void basic() {
        interface Test {
            int blob();
            
            int bean(int i);
            
            String name();
        }
        final Test test = Mirror.of(this).magic(Test.class);
        assert test != null;
        assert test.blob() == 6;
        assert test.bean(3) == 5;
        assert test.name().equals("Hello");
    }
    
    
}
