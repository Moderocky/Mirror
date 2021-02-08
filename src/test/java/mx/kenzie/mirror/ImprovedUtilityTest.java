package mx.kenzie.mirror;


import org.junit.Test;

public class ImprovedUtilityTest {
    
    
    @Test
    @SuppressWarnings("all")
    public void magicTest() {
        final Foo foo = new Foo("hello");
        assert foo != null;
        assert foo.name.equals("hello");
        assert foo.age == 0;
        assert foo.bar.number == 10;
        interface Access {
            String get$name();
            int get$age();
            void set$name(String name);
            void set$age(int age);
            Bar get$bar();
            String getName();
            int getAge();
        }
        final Access access = new Mirror<>(foo).magic(Access.class);
        assert access != null;
        assert access.get$age() == 0;
        assert access.getAge() == 0;
        assert access.get$name().equals("hello");
        assert access.getName().equals("hello");
        access.set$age(2);
        assert access.get$age() == 2;
        assert access.getAge() == 2;
        assert foo.age == 2;
        access.set$name("blob");
        assert access.get$name().equals("blob");
        assert access.getName().equals("blob");
        assert foo.name.equals("blob");
        final Bar bar = access.get$bar();
        assert bar != null;
        interface BarAccess {
            int get$number();
            void set$number(int number);
        }
        final BarAccess thing = new Mirror<>(bar).magic(BarAccess.class);
        assert thing != null;
        assert thing.get$number() == bar.number;
        assert thing.get$number() == 10;
        thing.set$number(6);
        assert bar.number == 6;
        assert thing.get$number() == 6;
        assert foo.bar.number == 6;
    }
    
    @Test
    @SuppressWarnings("all")
    public void directTest() {
        final Foo foo = new Foo("hello");
        assert foo != null;
        assert foo.name.equals("hello");
        assert foo.age == 0;
        assert foo.bar.number == 10;
        final Mirror<Foo> mirror = new Mirror(foo);
        assert mirror != null;
        final FieldMirror<Bar> field = mirror.<Bar>field("bar");
        final Bar bar = field.get();
        final Mirror<Bar> barMirror = mirror.<Bar>field("bar").getAndMirror();
        assert bar != null;
        assert bar == foo.bar;
        assert barMirror != null;
        assert barMirror.object == bar;
        mirror.field("name").set("blob");
        assert mirror.field("name").get() == "blob";
        assert foo.name.equals("blob");
        mirror.field("age").set(2);
        assert (int) mirror.<Integer>field("age").<Integer>get() == 2;
        assert foo.age == 2;
        assert bar.number == 10;
        barMirror.field("number").set(6);
        assert bar.number == 6;
        assert foo.bar.number == 6;
    }
    
    private static class Foo {
        
        private final String name;
        private final int age;
        public final Bar bar;
        
        private Foo(String name) {
            this.name = name;
            this.age = 0;
            this.bar = new Bar();
        }
    
        public String getName() {
            return name;
        }
    
        public int getAge() {
            return age;
        }
        
    }
    
    private static class Bar {
        
        public final int number;
        
        private Bar() {
            this.number = 10;
        }
        
    }
    
}
