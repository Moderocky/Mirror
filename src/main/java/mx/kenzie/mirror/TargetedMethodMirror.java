package mx.kenzie.mirror;

import java.lang.reflect.Method;

public final class TargetedMethodMirror<ReturnType>
    extends MethodMirror<ReturnType> {
    
    final Object target;
    
    public TargetedMethodMirror(Method object, Object target) {
        super(object);
        this.target = target;
    }
    
    public TargetedMethodMirror(Method object, Object target, Class<ReturnType> type) {
        super(object, type);
        this.target = target;
    }
    
    @Override
    public ReturnType invoke(Object... parameters) {
        return this.invokeOn(target, parameters);
    }
    
    public Object getTarget() {
        return target;
    }
    
}
