package mx.kenzie.mirror;

import org.junit.Test;

public class MagicFieldMirrorTest {
    
    @Test
    public void test() {
        final Secret secret = new Secret();
        assert secret != null;
        interface Keeper {
            int get$number();
            String get$string();
            String string();
        }
        final Keeper keeper = new Mirror<>(secret).magic(Keeper.class);
        assert keeper != null;
        assert keeper.get$number() == 6;
        assert keeper.get$string().equals("hello");
        assert keeper.string().equals("hi");
    }
    
    private static class Secret {
        private final int number = 6;
        private final String string = "hello";
        public String string() {
            return "hi";
        }
    }
    
}
