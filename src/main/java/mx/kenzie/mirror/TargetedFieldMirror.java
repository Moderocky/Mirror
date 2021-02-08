package mx.kenzie.mirror;

import mx.kenzie.mirror.error.CapturedReflectionException;

import java.lang.reflect.Field;

public final class TargetedFieldMirror<Type>
    extends FieldMirror<Type> {
    
    final Object target;
    
    public TargetedFieldMirror(Field object, Object target) {
        super(object);
        this.target = target;
    }
    
    public TargetedFieldMirror(Field object, Object target, Class<Type> type) {
        super(object, type);
        this.target = target;
    }
    
    @Override
    public void set(Type value) {
        this.set(target, value);
    }
    
    @Override
    public Type get() {
        return this.get(target);
    }
    
    public long getLong() {
        try {
            return object.getLong(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setLong(long l) {
        try {
            object.setLong(target, l);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public int getInt() {
        try {
            return object.getInt(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setInt(int i) {
        try {
            object.setInt(target, i);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public short getShort() {
        try {
            return object.getShort(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setShort(short s) {
        try {
            object.setShort(target, s);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public byte getByte() {
        try {
            return object.getByte(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setByte(byte b) {
        try {
            object.setByte(target, b);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public double getDouble() {
        try {
            return object.getDouble(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setDouble(double d) {
        try {
            object.setDouble(target, d);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public float getFloat() {
        try {
            return object.getFloat(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setFloat(float f) {
        try {
            object.setFloat(target, f);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public char getChar() {
        try {
            return object.getChar(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setChar(char c) {
        try {
            object.setChar(target, c);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public boolean getBoolean() {
        try {
            return object.getBoolean(target);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public void setBoolean(boolean z) {
        try {
            object.setBoolean(target, z);
        } catch (IllegalAccessException e) {
            throw new CapturedReflectionException(e);
        }
    }
    
    public Object getTarget() {
        return target;
    }
    
}
