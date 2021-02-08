package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.FieldAccessorStub;

import java.lang.reflect.Field;

public final class TargetedFastFieldMirror<Type>
    extends FastFieldMirror<Type> {
    
    final Object target;
    private final FieldAccessorStub stub;
    
    public TargetedFastFieldMirror(Field field, FieldAccessorStub object, Object target) {
        super(field, object, (Class<Type>) field.getType());
        this.target = target;
        this.stub = object;
    }
    
    public TargetedFastFieldMirror(Field field, FieldAccessorStub object, Object target, Class<Type> type) {
        super(field, object, type);
        this.target = target;
        this.stub = object;
    }
    
    @Override
    public void set(Type value) {
        stub.set(target, value);
    }
    
    @Override
    public Type get() {
        return (Type) stub.get(target);
    }
    
    public long getLong() {
        return stub.getLong(target);
    }
    
    public void setLong(long l) {
        stub.setLong(target, l);
    }
    
    public int getInt() {
        return stub.getInt(target);
    }
    
    public void setInt(int i) {
        stub.setInt(target, i);
    }
    
    public short getShort() {
        return stub.getShort(target);
    }
    
    public void setShort(short s) {
        stub.setShort(target, s);
    }
    
    public byte getByte() {
        return stub.getByte(target);
    }
    
    public void setByte(byte b) {
        stub.setByte(target, b);
    }
    
    public double getDouble() {
        return stub.getDouble(target);
    }
    
    public void setDouble(double d) {
        stub.setDouble(target, d);
    }
    
    public float getFloat() {
        return stub.getFloat(target);
    }
    
    public void setFloat(float f) {
        stub.setFloat(target, f);
    }
    
    public char getChar() {
        return stub.getChar(target);
    }
    
    public void setChar(char c) {
        stub.setChar(target, c);
    }
    
    public boolean getBoolean() {
        return stub.getBoolean(target);
    }
    
    public void setBoolean(boolean z) {
        stub.setBoolean(target, z);
    }
    
    public Object getTarget() {
        return target;
    }
    
}
