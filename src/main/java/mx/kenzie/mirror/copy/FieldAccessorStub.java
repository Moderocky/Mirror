package mx.kenzie.mirror.copy;

import mx.kenzie.mirror.note.Fake;

@Fake("jdk.internal.reflect.FieldAccessor")
public class FieldAccessorStub {
    
    public native Object get(Object obj);
    
    public native boolean getBoolean(Object obj);
    
    public native byte getByte(Object obj);
    
    public native char getChar(Object obj);
    
    public native short getShort(Object obj);
    
    public native int getInt(Object obj);
    
    public native long getLong(Object obj);
    
    public native float getFloat(Object obj);
    
    public native double getDouble(Object obj);
    
    public native void set(Object obj, Object value);
    
    public native void setBoolean(Object obj, boolean z);
    
    public native void setByte(Object obj, byte b);
    
    public native void setChar(Object obj, char c);
    
    public native void setShort(Object obj, short s);
    
    public native void setInt(Object obj, int i);
    
    public native void setLong(Object obj, long l);
    
    public native void setFloat(Object obj, float f);
    
    public native void setDouble(Object obj, double d);
    
}
