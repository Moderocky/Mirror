package mx.kenzie.mirror;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public interface ConstructorAccessor<Type> extends Accessor, ExecutableAccessor<Type, Constructor<Type>> {
    
    default Type newInstance(Object... arguments) {
        return this.invoke(arguments);
    }
    
    Type invoke(Object... arguments);
    
    Constructor<Type> reflect();
    
    Object getTarget();
    
    Class<?> getTargetType();
    
    abstract class ConstructorAccessorImpl<Type> implements ConstructorAccessor<Type> {
        
        protected int modifiers;
        protected boolean dynamic;
        public Constructor<?> handle;
        
        protected Object target;
        protected final Class<?> targetType;
        
        public ConstructorAccessorImpl(Type target) {
            this.target = target;
            this.targetType = target.getClass();
        }
        
        protected void verifyArray(Object[] objects, int length) {
            if (objects.length < length)
                throw new IllegalArgumentException("");
        }
        
        @Override
        public void setTarget(Object target) {
            this.target = target;
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
        
        public Constructor<Type> reflect() {
            return (Constructor<Type>) handle;
        }
        
        @Override
        public Object getTarget() {
            return target;
        }
        
        @Override
        public Class<?> getTargetType() {
            return targetType;
        }
    }
    
}
