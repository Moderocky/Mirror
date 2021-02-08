package mx.kenzie.mirror.copy;

import mx.kenzie.mirror.note.Mirrors;

@Mirrors(targetPath = "jdk.internal.reflect.FieldAccessor")
public interface FieldAccessor extends Reflected<Object> {
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    Object get(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    boolean getBoolean(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    byte getByte(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    char getChar(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    short getShort(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    int getInt(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    long getLong(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    float getFloat(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    double getDouble(Object obj) throws IllegalArgumentException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void set(Object obj, Object value)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setBoolean(Object obj, boolean z)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setByte(Object obj, byte b)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setChar(Object obj, char c)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setShort(Object obj, short s)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setInt(Object obj, int i)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setLong(Object obj, long l)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setFloat(Object obj, float f)
        throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Matches specification in {@link java.lang.reflect.Field}
     */
    void setDouble(Object obj, double d)
        throws IllegalArgumentException, IllegalAccessException;
}
