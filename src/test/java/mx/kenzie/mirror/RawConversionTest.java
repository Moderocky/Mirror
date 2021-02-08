package mx.kenzie.mirror;

import org.junit.Test;

public class RawConversionTest {
    
    @Test
    public void byteLong() {
        final byte initial = 127;
        final long converted = initial;
        final byte result = (byte) converted;
        assert result == initial;
    }
    
    @Test
    public void charLong() {
        final char initial = 'c';
        final long converted = initial;
        final char result = (char) converted;
        assert result == initial;
    }
    
    @Test
    public void shortLong() {
        final short initial = -30000;
        final long converted = initial;
        final short result = (short) converted;
        assert result == initial;
    }
    
}
