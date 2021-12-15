package mx.kenzie.mirror;

import mx.kenzie.glass.Window;

import java.lang.reflect.Modifier;

public interface ConstructorAccessor<Type> extends Window, Accessor, MethodAccessor<Type> {
    
    default Type newInstance(Object... arguments) {
        return this.invoke(arguments);
    }
    
    Type invoke(Object... arguments);
    
    abstract class ConstructorAccessorImpl<Type> extends WindowFrame implements ConstructorAccessor<Type> {
        
        protected int modifiers;
        protected boolean dynamic;
        
        public ConstructorAccessorImpl(Type target) {
            super(target);
        }
        
        protected void verifyArray(Object[] objects, int length) {
            if (objects.length < length)
                throw new IllegalArgumentException("");
        }
        
        public void setTarget(Type target) {
            if (!targetType.isAssignableFrom(target.getClass()))
                throw new IllegalArgumentException("New target must be of a compatible type.");
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
        
    }
    
}
