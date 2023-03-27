package mx.kenzie.mirror;

public interface ClassProvider {

    Class<?> loadClass(Class<?> target, String name, byte[] bytes);

}
