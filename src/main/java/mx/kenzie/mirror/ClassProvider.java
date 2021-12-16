package mx.kenzie.mirror;

interface ClassProvider {
    
    Class<?> loadClass(Class<?> target, String name, byte[] bytes);
    
}
