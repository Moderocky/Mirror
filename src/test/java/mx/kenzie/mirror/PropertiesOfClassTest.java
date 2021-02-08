package mx.kenzie.mirror;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class PropertiesOfClassTest {
    
    @Test
    public void test() {
        final Class<?> type = Foo.class.getClass();
        final Field[] apparent = type.getDeclaredFields();
        final Field[] fields = Utilities.getDeclaredFields(type);
        assert apparent.length < fields.length;
    }
    
    private static class Foo {
    
    }
    
}
