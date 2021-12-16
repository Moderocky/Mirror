package mx.kenzie.mirror.test;

import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.junit.Test;

import java.io.Serializable;

public class InternalAccessTest {
    
    private int number() {
        return 2;
    }
    
    @Test
    public void accessJavaInternals() throws Throwable {
        final Class<?> secrets = Class.forName("jdk.internal.access.SharedSecrets", false, ClassLoader.getSystemClassLoader());
        final MethodAccessor<?> accessor = Mirror.of(secrets)
            .unsafe()
            .method("getJavaLangAccess");
        final Object jla = accessor.invoke();
        assert jla != null;
    }
    
    @Test
    public void accessPrivateField() {
        {
            final MethodAccessor<Class<?>[]> accessor = Mirror.of(Class.class)
                .unsafe()
                .method("getInterfaces0");
            final Class<?>[] interfaces = accessor.invoke();
            assert interfaces[0] == Serializable.class;
        }
        
        { // check duplication doesn't occur
            final MethodAccessor<Class<?>[]> accessor = Mirror.of(Class.class)
                .unsafe()
                .method("getInterfaces0");
            final Class<?>[] interfaces = accessor.invoke();
            assert interfaces[0] == Serializable.class;
        }
        
    }
    
    @Test
    public void accessUnnamed() {
        final MethodAccessor<Integer> accessor = Mirror.of(this)
            .unsafe()
            .method("number");
        assert accessor.invoke() == 2;
    }
    
}
