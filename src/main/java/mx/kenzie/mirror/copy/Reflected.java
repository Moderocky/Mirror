package mx.kenzie.mirror.copy;

import org.jetbrains.annotations.NotNull;

public interface Reflected<Subject> {
    
    @NotNull Subject getOriginal();
    
    @NotNull Class<?> getOriginalClass();
    
}
