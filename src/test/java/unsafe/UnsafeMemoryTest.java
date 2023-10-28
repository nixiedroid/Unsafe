package unsafe;

import com.nixiedroid.unsafe.Unsafe.Memory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class UnsafeMemoryTest {
    private <T> void createUtilityObjectReflection(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            constructor.setAccessible(false);
        } catch (InvocationTargetException e) {
            throw (UnsupportedOperationException) e.getTargetException();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            fail("Should not have thrown any exception: \n " + e);
        }
    }
    @Test
    void constructor(){
        assertThrows(UnsupportedOperationException.class,  () ->createUtilityObjectReflection(Memory.class));
    }

    @Test
    void malloc() {

    }

    @Test
    void fill() {
    }

    @Test
    void empty() {
    }

    @Test
    void realloc() {
    }

    @Test
    void calloc() {
    }

    @Test
    void memcpy() {
    }

    @Test
    void testMemcpy() {
    }

    @Test
    void getByte() {
    }

    @Test
    void testGetByte() {
    }

    @Test
    void setByte() {
    }

    @Test
    void testSetByte() {
    }
}