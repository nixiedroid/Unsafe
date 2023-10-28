package unsafe;

import com.nixiedroid.unsafe.Unsafe.Memory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class UnsafeMemoryTest {
    private void createUtilityObjectReflection() {
        try {
            Constructor<Memory> constructor = Memory.class.getDeclaredConstructor();
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
    void constructor() {
        assertThrows(UnsupportedOperationException.class, this::createUtilityObjectReflection);
    }
}