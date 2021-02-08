package mx.kenzie.mirror.copy;

import mx.kenzie.mirror.note.Fake;

@Fake("jdk.internal.reflect.MethodAccessor")
public class MethodAccessorStub {
    
    public native Object invoke(Object obj, Object... args);
}
