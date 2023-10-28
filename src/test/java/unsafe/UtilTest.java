package unsafe;

import com.nixiedroid.unsafe.Util;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {
    private void createUtilityObjectReflection() {
        try {
            Constructor<Util> constructor = (Util.class).getDeclaredConstructor();
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
        assertThrows(UnsupportedOperationException.class, this::createUtilityObjectReflection);
    }

    @Test
    void performGc() {
        Object o = new Object();
        WeakReference<Object> ref = new WeakReference<>(o);
        o = null;
        try {
            Method m = Util.class.getDeclaredMethod("performGcHalting");
            m.setAccessible(true);
            m.invoke(null);
            m.setAccessible(false);
        } catch (Exception e) {
            fail("Should not have thrown Exception");
        }
        assertNull(o);
        assertNull(ref.get());
    }

    @Test
    void testToString() {
        assertEquals("12",Util.toString((byte) 0x12));
    }

    @Test
    void testToString1() {
        assertEquals("12, 34, 56, ab",Util.toString(new byte[]{0x12,0x34,0x56, (byte) 0xAB}));
    }

    @Test
    void throwUtilityClassException() {
        assertThrowsExactly(UnsupportedOperationException.class, Util::throwUtilityClassException);
    }
}