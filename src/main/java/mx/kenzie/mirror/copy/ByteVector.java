package mx.kenzie.mirror.copy;

public interface ByteVector {
    
    void add(byte $0);
    
    byte get(int $0);
    
    void put(int $0, byte $1);
    
    int getLength();
    
    void trim();
    
    byte[] getData();
    
}
