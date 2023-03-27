package mx.kenzie.mirror.test;

import mx.kenzie.mirror.Mirror;
import org.junit.Test;

public class MagicMirrorTest {

    public static int a = 1;
    @SuppressWarnings("all")
    private static int b = 2;
    public int c = 3;
    @SuppressWarnings("all")
    private int d = 4;

    private static int bean(int i) {
        return i + 2;
    }

    public static int box(long l) {
        return (int) l;
    }

    private int blob() {
        return 6;
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

    @Test
    public void intrinsic() {
        interface Test {
            int blob();

            int bean(int i);

            int box(long l);

            String name();

            int $a();

            void $a(int i);

            int $b();

            void $b(int i);

            int $c();

            void $c(int i);

            int $d();

            void $d(int i);
        }
        final Test test = Mirror.of(this).magicIntrinsic(Test.class);
        assert test != null;
        assert test.blob() == 6;
        assert test.bean(3) == 5;
        assert test.name().equals("Hello");
        assert test.box(10L) == 10;
        test.$a(2);
        test.$b(2);
        test.$c(2);
        test.$d(2);
        assert test.$a() == 2;
        assert test.$b() == 2;
        assert test.$c() == 2;
        assert test.$d() == 2;
    }


}
