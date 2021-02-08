package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.FieldAccessor;
import mx.kenzie.mirror.error.CapturedReflectionException;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldMirrorSpeedTest {
    
    @Test
    public void test() throws Throwable {
//        System.out.println("Reflection via native accessor: " + a1() + " nanos.");
//        System.out.println("Reflection via field object: " + a2() + " nanos.");
        assert a1() > a2();
    }
    
    public long a1() throws Throwable {
        final Field field = Sealed.class.getDeclaredField("STRING");
        final TFieldAccessorMirror<String> mirror = new TFieldAccessorMirror<>(field);
        final long start = System.nanoTime(), end;
        for (int i = 0; i < 100; i++) {
            final String string = mirror.get(null);
            assert string.equals("hello");
            mirror.set(null, "goodbye");
            assert mirror.get(null).equals("goodbye");
            mirror.set(null, "hello");
        }
        end = System.nanoTime();
        return end - start;
    }
    
    public long a2() throws Throwable {
        final Field field = Sealed.class.getDeclaredField("STRING");
        final FieldMirror<String> mirror = new FieldMirror<>(field);
        final long start = System.nanoTime(), end;
        for (int i = 0; i < 100; i++) {
            final String string = mirror.get(null);
            assert string.equals("hello");
            mirror.set(null, "goodbye");
            assert mirror.get(null).equals("goodbye");
            mirror.set(null, "hello");
        }
        end = System.nanoTime();
        return end - start;
    }
    
    private static class Sealed {
        
        private static final String STRING = "hello";
        
    }
    
    
    public static class TFieldAccessorMirror<T> extends AbstractMirror<Field> {
        
        protected final FieldAccessor object;
        
        public TFieldAccessorMirror(Field object, Class<T> type) {
            super(object);
            Utilities.prepareForAccess(object);
            this.object = Utilities.getMirroredAccessor(Utilities
                .getExistingAccessor(object)
            );
        }
        
        public TFieldAccessorMirror(Field object) {
            this(object, (Class<T>) object.getType());
        }
        
        public Class<?> getType() {
            return type;
        }
        
        public Annotation[] getAnnotations() {
            return super.object.getDeclaredAnnotations();
        }
        
        public String getName() {
            return super.object.getName();
        }
        
        @SuppressWarnings("unchecked")
        public T get(Object target) throws CapturedReflectionException {
            return (T) object.get(target);
        }
        
        public double getDouble(Object target) {
            return object.getDouble(target);
        }
        
        public float getFloat(Object target) {
            return object.getFloat(target);
        }
        
        public long getLong(Object target) {
            return object.getLong(target);
        }
        
        public int getInt(Object target) {
            return object.getInt(target);
        }
        
        public short getShort(Object target) {
            return object.getShort(target);
        }
        
        public byte getByte(Object target) {
            return object.getByte(target);
        }
        
        public boolean getBoolean(Object target) {
            return object.getBoolean(target);
        }
        
        public char getChar(Object target) {
            return object.getChar(target);
        }
        
        public void set(Object target, T value) throws CapturedReflectionException {
            try {
                object.set(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setDouble(Object target, double value) throws CapturedReflectionException {
            try {
                object.setDouble(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setFloat(Object target, float value) throws CapturedReflectionException {
            try {
                object.setFloat(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setLong(Object target, long value) throws CapturedReflectionException {
            try {
                object.setLong(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setInt(Object target, int value) throws CapturedReflectionException {
            try {
                object.setInt(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setShort(Object target, short value) throws CapturedReflectionException {
            try {
                object.setShort(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setByte(Object target, byte value) throws CapturedReflectionException {
            try {
                object.setByte(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setBoolean(Object target, boolean value) throws CapturedReflectionException {
            try {
                object.setBoolean(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
        
        public void setChar(Object target, char value) throws CapturedReflectionException {
            try {
                object.setChar(target, value);
            } catch (IllegalAccessException e) {
                throw new CapturedReflectionException(e);
            }
        }
    }
}
