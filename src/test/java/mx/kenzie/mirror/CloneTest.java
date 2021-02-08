package mx.kenzie.mirror;

import org.junit.Test;

public class CloneTest {
    
    @Test
    public void shallow() {
        final Foo foo = new Foo();
        final Foo second = new Mirror<>(foo).shallowCopy();
        assert foo != second;
        assert foo.bar == second.bar;
        assert foo.looong == second.looong;
        assert foo.string == second.string;
        assert foo.bar.number == second.bar.number;
        assert foo.bar.string == second.bar.string;
    }
    
    @Test
    public void deep() {
        final Foo foo = new Foo();
        final Foo second = new Mirror<>(foo).deepCopy();
        assert foo != second;
        assert foo.bar != second.bar;
        assert foo.looong == second.looong;
        assert foo.string.equals(second.string);
        assert foo.bar.number == second.bar.number;
        assert foo.bar.string.equals(second.bar.string);
    }
    
    private static class Foo {
        public final long looong = 2000;
        public final Bar bar = new Bar();
        public final String string = "hello";
    }
    
    private static class Bar {
        public final int number = 1;
        public final String string = "hoi";
    }
    
}
