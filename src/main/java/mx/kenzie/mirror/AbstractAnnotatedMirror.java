package mx.kenzie.mirror;

import mx.kenzie.mirror.copy.Reflected;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.constant.Constable;
import java.lang.reflect.AnnotatedElement;

/**
 * A mirror for an annotated type.
 *
 * @param <Subject> the target
 */
public abstract class AbstractAnnotatedMirror<Subject extends AnnotatedElement>
    implements Reflected<Subject> {
    
    protected final Subject object;
    protected final Class<?> cls;
    
    public AbstractAnnotatedMirror(Subject object) {
        this.object = object;
        this.cls = object.getClass();
    }
    
    public Annotation[] getAnnotations() {
        return object.getDeclaredAnnotations();
    }
    
    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return object.getAnnotation(type) != null;
    }
    
    public <ReturnType extends Annotation> ReturnType getQuasiAnnotation(Class<ReturnType> type) {
        final ReturnType note = getAnnotation(type);
        if (note != null) return note;
        return Utilities.allocateInstance(type);
    }
    
    public <ReturnType extends Annotation> ReturnType getAnnotation(Class<ReturnType> type) {
        return object.getAnnotation(type);
    }
    
    @Override
    public @NotNull Subject getOriginal() {
        return object;
    }
    
    @Override
    public @NotNull Class<?> getOriginalClass() {
        return cls;
    }
    
    public abstract Constable getHandle();
    
}
