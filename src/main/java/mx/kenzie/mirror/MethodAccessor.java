package mx.kenzie.mirror;

import mx.kenzie.glass.Window;

import java.lang.reflect.Modifier;

public interface MethodAccessor<Return> extends Window, Accessor {
    
    Return invoke(Object... arguments);
    
    abstract class MethodAccessorImpl<Thing, Return> extends WindowFrame implements MethodAccessor<Return> {
        
        protected int modifiers;
        protected boolean dynamic;
        
        public MethodAccessorImpl(Thing target) {
            super(target);
        }
        
        protected void verifyArray(Object[] objects, int length) {
            if (objects.length < length)
                throw new IllegalArgumentException("Hello");
        }
        
        @Override
        public void setTarget(Object target) {
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
