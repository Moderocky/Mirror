package mx.kenzie.mirror;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface MethodAccessor<Return> extends ExecutableAccessor<Return, Method>, Accessor {
    
    Return invoke(Object... arguments);
    
    Method reflect();
    
    Object getTarget();
    
    Class<?> getTargetType();
    
    abstract class MethodAccessorImpl<Thing, Return> implements MethodAccessor<Return> {
        
        protected final Class<?> targetType;
        public Method handle;
        protected int modifiers;
        protected boolean dynamic;
        protected Object target;
        
        public MethodAccessorImpl(Thing target) {
            this.target = target;
            this.targetType = target.getClass();
        }
        
        protected void verifyArray(Object[] objects, int length) {
            if (objects.length < length)
                throw new IllegalArgumentException("Parameter count " + objects.length + " is lower than required " + length);
        }
        
        @Override
        public int getModifiers() {
            return modifiers;
        }
        
        @Override
        public boolean isStatic() {
            return Modifier.isStatic(modifiers);
        }
        
        @Override
        public boolean isDynamicAccess() {
            return dynamic;
        }
        
        @Override
        public Method reflect() {
            return handle;
        }
        
        @Override
        public Object getTarget() {
            return target;
        }
        
        @Override
        public void setTarget(Object target) {
            if (!targetType.isAssignableFrom(target.getClass()))
                throw new IllegalArgumentException("New target must be of a compatible type.");
            this.target = target;
        }
        
        @Override
        public Class<?> getTargetType() {
            return targetType;
        }
    }
    
}

interface ExecutableAccessor<Return, Handle extends Executable> extends Accessor {
    
    Return invoke(Object... arguments);
    
    Handle reflect();
    
}
