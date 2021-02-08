package mx.kenzie.mirror;

import mx.kenzie.mirror.error.CapturedReflectionException;

import java.lang.reflect.Field;

/**
 * This class on its own will very rarely be used, except as a type to hide
 * another sort of field mirror.
 * <p>
 * This is technically functional on its own.
 *
 * @param <Type> The field's type or wrapper type
 */
public sealed class FieldMirror<Type> extends AbstractAnnotatedMirror<Field> permits FastFieldMirror, TargetedFieldMirror {
    
    protected final Class<?> type;
    
    protected FieldMirror(Field object) {
        this(object, (Class<Type>) object.getType());
    }
    
    protected FieldMirror(Field object, Class<Type> type) {
        super(object);
        Utilities.prepareForAccess(object);
        this.type = type;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public String getName() {
        return object.getName();
    }
    
    public void set(Type value) {
        throw new IllegalStateException("This has no default implementation - for use by child classes.");
    }
    
    public Mirror<Type> getAndMirror(Object target) {
        return new Mirror<>(get(target));
    }
    
    @SuppressWarnings("unchecked")
    public Type get(Object target) throws CapturedReflectionException {
        try {
            return (Type) object.get(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public Mirror<Type> getAndMirror() {
        return new Mirror<>(get());
    }
    
    public Type get() {
        throw new IllegalStateException("This has no default implementation - for use by child classes.");
    }
    
    public double getDouble(Object target) throws CapturedReflectionException {
        try {
            return object.getDouble(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public float getFloat(Object target) throws CapturedReflectionException {
        try {
            return object.getFloat(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public long getLong(Object target) throws CapturedReflectionException {
        try {
            return object.getLong(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public int getInt(Object target) throws CapturedReflectionException {
        try {
            return object.getInt(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public short getShort(Object target) throws CapturedReflectionException {
        try {
            return object.getShort(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public byte getByte(Object target) throws CapturedReflectionException {
        try {
            return object.getByte(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public boolean getBoolean(Object target) throws CapturedReflectionException {
        try {
            return object.getBoolean(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public char getChar(Object target) throws CapturedReflectionException {
        try {
            return object.getChar(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void set(Object target, Type value) throws CapturedReflectionException {
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
