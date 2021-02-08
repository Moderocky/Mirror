package mx.kenzie.mirror.note;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Mirrors {
    
    String targetPath() default "";
    
    Class<?> target() default Void.class;
    
}
