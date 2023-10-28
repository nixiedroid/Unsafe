package unsafe;

import com.nixiedroid.unsafe.type.Size;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SizeTest {

    @Test
    void size() {
        assertDoesNotThrow(()->new Size(1));
        assertDoesNotThrow(()->new Size(0));
        assertThrows(IllegalArgumentException.class,()->new Size(-1));
    }
}