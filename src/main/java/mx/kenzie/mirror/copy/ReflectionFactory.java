package mx.kenzie.mirror.copy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionFactory {
    public native Constructor copyConstructor(Constructor $0);
    public native Object newInstance(Constructor $0, Object[] $1, Class $2);
    public native Class[] getExecutableSharedParameterTypes(Executable $0);
    public native Method copyMethod(Method $0);
    public native Field copyField(Field $0);
    public native byte[] getExecutableTypeAnnotationBytes(Executable $0);
    public native FieldAccessor newFieldAccessor(Field $0, boolean $1);
    public native Object newMethodAccessor(Method $0);
    public native Object getConstructorAccessor(Constructor $0);
    public native Object newConstructorAccessor(Constructor $0);
    public native void setConstructorAccessor(Constructor $0, Object $1);
    public native Constructor generateConstructor(Class $0, Constructor $1);
    public native Constructor newConstructor(Class $0, Class[] $1, Class[] $2, int $3, int $4, String $5, byte[] $6, byte[] $7);
    public native Method leafCopyMethod(Method $0);
    public native boolean superHasAccessibleConstructor(Class $0);
    public native Object findReadWriteObjectForSerialization(Class $0, String $1, Class $2);
    public native Object getReplaceResolveForSerialization(Class $0, String $1);
    public native Constructor newConstructorForExternalization(Class $0);
    public native Constructor newConstructorForSerialization(Class $0);
    public native Constructor newConstructorForSerialization(Class $0, Constructor $1);
    public native Object readObjectForSerialization(Class $0);
    public native Object readObjectNoDataForSerialization(Class $0);
    public native Object writeObjectForSerialization(Class $0);
    public native Object writeReplaceForSerialization(Class $0);
    public native Object readResolveForSerialization(Class $0);
    public native boolean hasStaticInitializerForSerialization(Class $0);
    public native Constructor newOptionalDataExceptionForSerialization();
}
