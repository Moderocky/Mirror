package mx.kenzie.mirror;

import mx.kenzie.glass.Window;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public interface ConstructorAccessor<Type> extends Window, Accessor, ExecutableAccessor<Type, Constructor<Type>> {
    
    default Type newInstance(Object... arguments) {
        return this.invoke(arguments);
    }
    
    Type invoke(Object... arguments);
    
    Constructor<Type> reflect();
    
    abstract class ConstructorAccessorImpl<Type> extends WindowFrame implements ConstructorAccessor<Type> {
        
        protected int modifiers;
        protected boolean dynamic;
        public Constructor<?> handle;
        
        public ConstructorAccessorImpl(Type target) {
            super(target);
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
    }
    
}
