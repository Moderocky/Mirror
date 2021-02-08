package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.ByteVector;
import mx.kenzie.mirror.copy.ClassFileAssembler;
import mx.kenzie.mirror.copy.ClassFileConstants;
import mx.kenzie.mirror.copy.Reflected;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.sql.Connection;

public class BytecodeAssemblerTest {
    
    private static final short NUM_BASE_CPOOL_ENTRIES = (short) 12;
    private static final short NUM_METHODS = (short) 2;
    private static final short NUM_SERIALIZATION_CPOOL_ENTRIES = (short) 2;
    private static final short NUM_COMMON_CPOOL_ENTRIES = (short) 30;
    private static final short NUM_BOXING_CPOOL_ENTRIES = (short) 73;
    
    final ClassMirror<?> vectorClass = Mirror.ofClass("jdk.internal.reflect.ByteVector");
    final ClassMirror<?> factoryClass = Mirror.ofClass("jdk.internal.reflect.ByteVectorFactory");
    final ClassMirror<?> assemblerClass = Mirror.ofClass(InternalAccessor.classFileAssemblerClass);
    
    @Test
    public <
        Assembler extends ClassFileAssembler & ClassFileConstants & Reflected<Object>,
        Vector extends ByteVector & Reflected<Object>
        >
    
    void test() {
        assert factoryClass != null;
        assert assemblerClass != null;
        assert vectorClass != null;
        final Object vector = factoryClass.method("create").invoke();
        assert vector != null;
        final Object maker = assemblerClass.newInstance(vector);
        assert maker != null;
        final Assembler assembler = new Mirror<>(maker).magic(ClassFileAssembler.class,
            ClassFileConstants.class,
            Reflected.class);
        assert assembler != null;
        assembler.emitMagicAndVersion();
        assembler.opc_astore_1();
        assembler.opc_goto((short) 10);
        assembler.emitByte((byte) 2);
        assembler.emitByte(Assembler.CONSTANT_NameAndType);
        final Object object = assembler.getData();
        assert vectorClass.getOriginal().isInstance(object);
        final Vector data = new Mirror<>(object).magic(ByteVector.class,
            Reflected.class);
        assert data.getLength() > 0;
    }
    
    @Test
    public void createConstructor() throws Throwable {
        final Constructor<?> constructor = Foo.class.getConstructor();
        final Object object = InternalAccessor.internalFactory.newConstructorAccessor(constructor);
        assert object != null;
        final Constructor<Foo> test = Utilities.createConstructor(Foo.class);
        assert test != null;
        final Foo foo = test.newInstance();
        assert foo != null;
    }
    
    private static class Foo {
        
        public Foo() {
        
        }
        
    }
    
}
