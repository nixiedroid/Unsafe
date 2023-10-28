package unsafe.type;

import com.nixiedroid.unsafe.type.NativeChunk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NativeChunkTest {
    final static byte validValue = (byte) 42;
    final static int size = 4;
    @Test
    public void createArray(){
        NativeChunk canary = new NativeChunk(size);
        assertNotNull(canary);
        assertEquals(size, canary.size());
        canary.free();
    }
    @Test
    public void testZero(){
        NativeChunk canary = new NativeChunk(size);
        for (int i = 0; i < canary.size(); i++) {
            assertEquals((byte)0, canary.get(i));
        }
        canary.free();
    }
    @Test
    public void testAddByte(){
        NativeChunk canary = new NativeChunk(size);
        canary.set(0, validValue);
        assertEquals(canary.get(0),validValue);
        canary.free();
    }

    @Test
    public void testToString(){
        NativeChunk canary = new NativeChunk(size);
        canary.set(0, validValue);
        assertEquals(canary.toString(),"Native Chunk: [2a,00,00,00]");
        canary.free();
    }
    @Test
    public void testOutOfBounds(){
        NativeChunk canary = new NativeChunk(size);
        assertThrows(IndexOutOfBoundsException.class, () -> canary.get(size+1));
        assertThrows(IndexOutOfBoundsException.class, () -> canary.get(size));
        assertThrows(IndexOutOfBoundsException.class, () -> canary.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> canary.set(size,validValue));
        assertThrows(IndexOutOfBoundsException.class, () -> canary.set(size+1,validValue));
        assertThrows(IndexOutOfBoundsException.class, () -> canary.set(-1,validValue));
        canary.free();
    }
    @Test
    public void testInvalidArraySize(){
        assertThrows(IllegalArgumentException.class, () -> new NativeChunk(-1));
    }
    @Test
    public void testUseAfterFree(){
        NativeChunk canary = new NativeChunk(size);
        canary.set(0,validValue);
        assertEquals(validValue,canary.get(0));
        canary.free();
        assertThrows(UnsupportedOperationException.class, () -> canary.get(0) );
    }
    @Test
    public void testDoubleFree(){
        NativeChunk canary = new NativeChunk(1);
        canary.set(0,validValue);
        assertEquals(validValue,canary.get(0));
        canary.free();
        assertThrows(UnsupportedOperationException.class, canary::free);
    }


}
