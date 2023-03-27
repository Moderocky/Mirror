package mx.kenzie.mirror;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public interface FieldAccessor<Type> extends Accessor {

    void set(Object value);

    Field reflect();

    Class<?> getTargetType();

    default Mirror<Type> mirror() {
        return Mirror.of(get());
    }

    Type get();

    Object getTarget();

    abstract class FieldAccessorImpl<Thing, Type> implements FieldAccessor<Type> {

        protected final Class<?> targetType;
        public Field handle;
        protected int modifiers;
        protected boolean dynamic;
        protected Class<Type> type;
        protected Object target;

        public FieldAccessorImpl(Thing target) {
            this.target = target;
            this.targetType = target.getClass();
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

        public Field reflect() {
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

    class RelayedFieldAccessor<Thing, Type> extends FieldAccessorImpl<Thing, Type> {

        private final MethodAccessor<Type> getter;
        private final MethodAccessor<Void> setter;

        public RelayedFieldAccessor(MethodAccessor<Type> getter, MethodAccessor<Void> setter) {
            super((Thing) Object.class); // prevents the exception being thrown by super
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public Type get() {
            return getter.invoke();
        }

        @Override
        public void set(Object value) {
            setter.invoke(value);
        }

    }

}
