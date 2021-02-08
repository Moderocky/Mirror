package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.Reflected;
import mx.kenzie.mirror.error.CapturedReflectionException;
import mx.kenzie.mirror.note.ShouldBe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Invoker<Subject> implements Reflected<Subject>, InvocationHandler {
    
    private static final Method GET_ORIGINAL;
    private static final Method GET_ORIGINAL_CLASS;
    
    static {
        try {
            GET_ORIGINAL = Invoker.class.getMethod("getOriginal");
            GET_ORIGINAL_CLASS = Invoker.class.getMethod("getOriginalClass");
        } catch (NoSuchMethodException e) { // This should never happen :(
            throw new CapturedReflectionException(e);
        }
    }
    
    final Subject original;
    final Class<Subject> cls;
    final HashMap<MethodErasure, Method> erasures;
    final HashMap<String, Field> fields;
    private boolean hasInternalGetter = false;
    private boolean hasInternalSetter = false;
    
    @SuppressWarnings("unchecked")
    public Invoker(Subject original) {
        this.original = original;
        this.cls = (Class<Subject>) original.getClass();
        this.erasures = new HashMap<>();
        this.fields = new HashMap<>();
        populateMethodAccessors();
        populateFieldAccessors();
    }
    
    private void populateMethodAccessors() {
        for (final Method method : cls.getDeclaredMethods()) {
            if (method.getName().startsWith("get$")) hasInternalGetter = true;
            else if (method.getName().startsWith("set$")) hasInternalSetter = true;
            erasures.put(new MethodErasure(method), method);
            Utilities.prepareForAccess(method);
        }
        {
            erasures.put(new MethodErasure(GET_ORIGINAL), GET_ORIGINAL);
            erasures.put(new MethodErasure(GET_ORIGINAL_CLASS), GET_ORIGINAL_CLASS);
        }
    }
    
    private void populateFieldAccessors() {
        for (final Field field : Utilities.getDeclaredFields(cls)) {
            fields.put(field.getName(), field);
            Utilities.prepareForAccess(field);
        }
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String name = method.getName();
        if (args == null && name.equals("getOriginal")) return getOriginal();
        else if (args == null && name.equals("getOriginalClass")) return getOriginalClass();
        check:
        {
            if (name.startsWith("get$") && !hasInternalGetter) break check;
            else if (name.startsWith("set$") && !hasInternalSetter) break check;
            for (Map.Entry<MethodErasure, Method> entry : erasures.entrySet()) {
                if (entry.getKey().matches(method)) return entry.getValue().invoke(original, args);
            }
        }
        if (name.startsWith("get$")) {
            final String potential = name.substring(4);
            return fields.get(potential).get(original);
        } else if (name.startsWith("set$") && args != null && args.length > 0) {
            final String potential = name.substring(4);
            final Field field = fields.get(potential);
            if (field == null) return null;
            field.set(original, args[0]);
        }
        return null;
    }
    
    @Override
    public @NotNull Subject getOriginal() {
        return original;
    }
    
    @Override
    public @NotNull Class<Subject> getOriginalClass() {
        return cls;
    }
    
    protected static class MethodErasure {
        
        public final String name;
        public final Class<?>[] parameters;
        public final Class<?> returnType;
        
        public MethodErasure(Method method) {
            this.name = method.getName();
            this.parameters = method.getParameterTypes();
            this.returnType = method.getReturnType();
        }
        
        @Override
        public int hashCode() {
            int result = Objects.hash(name, returnType);
            result = 31 * result + Arrays.hashCode(parameters);
            return result;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof MethodErasure erasure) {
                return Objects.equals(name, erasure.name)
                    && Arrays.equals(parameters, erasure.parameters)
                    && Objects.equals(returnType, erasure.returnType);
            } else if (o instanceof Method method) {
                return matches(method);
            }
            return false;
        }
        
        public boolean matches(final Method method) {
            if (!method.getName().equals(name)) return false;
            if (!method.getReturnType().isAssignableFrom(returnType)) return false;
            if (method.getParameterCount() != parameters.length) return false;
            final Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                final Parameter parameter = parameters[i];
                final Class<?> matcher = this.parameters[i];
                final ShouldBe be = parameter.getAnnotation(ShouldBe.class);
                if (be != null) {
                    final Class<?> cls = Utilities.forName(be.value());
                    if (cls == null) return false;
                    if (!cls.equals(matcher)) return false;
                } else {
                    if (!matcher.isAssignableFrom(parameter.getType())) return false;
                }
            }
            return true;
        }
    }
    
}
