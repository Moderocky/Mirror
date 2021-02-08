package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.MethodAccessorStub;

import java.lang.reflect.Method;

public final class TargetedFastMethodMirror<ReturnType>
    extends FastMethodMirror<ReturnType> {
    
    final Object target;
    private final MethodAccessorStub stub;
    
    TargetedFastMethodMirror(Method method, MethodAccessorStub object, Object target) {
        super(method, object, (Class<ReturnType>) method.getReturnType());
        this.stub = object;
        this.target = target;
    }
    
    public TargetedFastMethodMirror(Method method, MethodAccessorStub object, Object target, Class<ReturnType> type) {
        super(method, object, type);
        this.stub = object;
        this.target = target;
    }
    
    @Override
    public ReturnType invoke(Object... parameters) {
        return (ReturnType) stub.invoke(target, parameters);
    }
    
    public Object getTarget() {
        return target;
    }
    
}
