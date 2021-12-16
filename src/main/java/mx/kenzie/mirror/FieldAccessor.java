package mx.kenzie.mirror;

import mx.kenzie.glass.Window;

import java.lang.reflect.Modifier;

public interface FieldAccessor<Type> extends Window, Accessor {
    
    Type get();
    
    void set(Object value);
    
    default Mirror<Type> mirror() {
        return Mirror.of(get());
    }
    
    abstract class FieldAccessorImpl<Thing, Type> extends WindowFrame implements FieldAccessor<Type> {
        
        protected int modifiers;
        protected boolean dynamic;
        protected Class<Type> type;
        
        public FieldAccessorImpl(Thing target) {
            super(target);
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
