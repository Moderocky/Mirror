package mx.kenzie.mirror;

import org.junit.Test;

public class FastMirrorTest {
    
    @Test
    @SuppressWarnings("all")
    public void methodTest() {
        final Foo foo = new Foo(100);
        assert foo != null;
        final Mirror<Foo> mirror = new Mirror<>(foo);
        assert (int) mirror.fastMethod("box").invoke() == 3;
        assert (long) mirror.fastMethod("box", long.class).invoke(10L) == 10L;
        assert (int) mirror.fastMethod("getBlob").invoke() == 100;
    }
    
    @Test
    @SuppressWarnings("all")
    public void fieldTest() {
        final Foo foo = new Foo(100);
        assert foo != null;
        final Mirror<Foo> mirror = new Mirror<>(foo);
        final FastFieldMirror<Integer> field = mirror.fastField("number");
        assert field.getInt() == 6;
        field.setInt(7);
        assert field.getInt() == 7;
        assert foo.number == 7;
        assert foo.blob == 100;
        mirror.fastField("blob").setInt(66);
        assert foo.blob == 66;
    }
    
    @Test
    @SuppressWarnings("all")
    public void alteringFinalStuff() {
        final String string = "hello there";
        assert string != null;
        assert string.equals("hello there");
        final Mirror<String> mirror = new Mirror<>(string);
        final byte[] bytes = (byte[]) mirror.fastField("value").get();
        assert bytes != null;
        assert bytes.length == 11;
        bytes[0] = 105;
        assert string.equals("iello there");
        bytes[0] = 103;
        assert string.equals("gello there");
    }
    
    private static class Foo {
        private final int number;
        private final int blob;
        
        private Foo(int blob) {
            this.blob = blob;
            this.number = 6;
        }
        
        private int getBlob() {
            return blob;
        }
        
        private int box() {
            return 3;
        }
    
        private long box(long l) {
            return l;
        }
    }
    
}
