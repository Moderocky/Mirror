package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.FieldAccessorStub;
import mx.kenzie.mirror.error.CapturedReflectionException;

import java.lang.reflect.Field;

/**
 * A fast field accessor mirror.
 * @param <Type> the field type
 */
public sealed class FastFieldMirror<Type>
    extends FieldMirror<Type>
    permits TargetedFastFieldMirror {
    
    protected final Class<?> type;
    private final FieldAccessorStub stub;
    
    FastFieldMirror(Field field, FieldAccessorStub object, Class<Type> type) {
        super(field);
        this.stub = object;
        this.type = type;
    }
    
    public void set(Type value) {
        throw new IllegalStateException("This has no default implementation - for use by child classes.");
    }
    
    public Mirror<Type> getAndMirror(Object target) {
        return new Mirror<>(get(target));
    }
    
    @SuppressWarnings("unchecked")
    public Type get(Object target) throws CapturedReflectionException {
        return (Type) stub.get(target);
    }
    
    public Mirror<Type> getAndMirror() {
        return new Mirror<>(get());
    }
    
    public Type get() {
        throw new IllegalStateException("This has no default implementation - for use by child classes.");
    }
    
    public double getDouble(Object target) throws CapturedReflectionException {
        try {
            return stub.getDouble(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public float getFloat(Object target) throws CapturedReflectionException {
        try {
            return stub.getFloat(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public long getLong(Object target) throws CapturedReflectionException {
        try {
            return stub.getLong(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public int getInt(Object target) throws CapturedReflectionException {
        try {
            return stub.getInt(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public short getShort(Object target) throws CapturedReflectionException {
        try {
            return stub.getShort(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public byte getByte(Object target) throws CapturedReflectionException {
        try {
            return stub.getByte(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public boolean getBoolean(Object target) throws CapturedReflectionException {
        try {
            return stub.getBoolean(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public char getChar(Object target) throws CapturedReflectionException {
        try {
            return stub.getChar(target);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void set(Object target, Type value) throws CapturedReflectionException {
        try {
            stub.set(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setDouble(Object target, double value) throws CapturedReflectionException {
        try {
            object.setDouble(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setFloat(Object target, float value) throws CapturedReflectionException {
        try {
            stub.setFloat(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setLong(Object target, long value) throws CapturedReflectionException {
        try {
            stub.setLong(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setInt(Object target, int value) throws CapturedReflectionException {
        try {
            stub.setInt(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setShort(Object target, short value) throws CapturedReflectionException {
        try {
            stub.setShort(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setByte(Object target, byte value) throws CapturedReflectionException {
        try {
            stub.setByte(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setBoolean(Object target, boolean value) throws CapturedReflectionException {
        try {
            stub.setBoolean(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setChar(Object target, char value) throws CapturedReflectionException {
        try {
            stub.setChar(target, value);
        } catch (Throwable e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    // For child implementations
    
    public native long getLong();
    
    public native void setLong(long l);
    
    public native int getInt();
    
    public native void setInt(int i);
    
    public native short getShort();
    
    public native void setShort(short s);
    
    public native byte getByte();
    
    public native void setByte(byte b);
    
    public native double getDouble();
    
    public native void setDouble(double d);
    
    public native float getFloat();
    
    public native void setFloat(float f);
    
    public native char getChar();
    
    public native void setChar(char c);
    
    public native boolean getBoolean();
    
    public native void setBoolean(boolean z);
    
}
