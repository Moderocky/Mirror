package mx.kenzie.mirror;

public interface Accessor {
    
    int getModifiers();
    
    boolean isStatic();
    
    boolean isDynamicAccess();
    
}
