package mx.kenzie.mirror;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class ProxyAccessor<Type> implements InvocationHandler {
    
    protected final Mirror<Type> mirror;
    protected final Map<MethodErasure, MethodAccessor<?>> methods = new HashMap<>();
    
    ProxyAccessor(Mirror<Type> mirror) {
        this.mirror = mirror;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        final Object[] parameters;
        if (args == null) parameters = new Object[0];
        else parameters = args;
        final MethodErasure erasure = new MethodErasure(method);
        if (!methods.containsKey(erasure))
            methods.put(erasure, mirror.method(method.getName(), method.getParameterTypes()));
        final MethodAccessor<?> accessor = methods.get(erasure);
        return accessor.invoke(parameters);
    }
    
    record MethodErasure(String name, Class<?>... parameters) {
        
        public MethodErasure(Method method) {
            this(method.getName(), method.getParameterTypes());
        }
        
    }
}
