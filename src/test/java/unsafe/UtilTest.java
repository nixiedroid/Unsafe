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

    @SuppressWarnings("ConstantValue")
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
    void throwUtilityClassException() {
        assertThrowsExactly(UnsupportedOperationException.class, Util::throwUtilityClassException);
    }
}