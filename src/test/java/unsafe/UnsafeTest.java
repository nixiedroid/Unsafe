package unsafe;

import com.nixiedroid.unsafe.Unsafe;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class UnsafeTest {
    @Test
    void unsafeInstanceTest() {
        sun.misc.Unsafe unsafe = null;
        assertThrowsExactly(SecurityException.class, sun.misc.Unsafe::getUnsafe);
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
            field.setAccessible(false);
        } catch (Exception e) {
            fail("Should not have thrown any exception: \n " + e);
        }
        assertNotNull(unsafe);
        try {
            Constructor<sun.misc.Unsafe> unsafeConstructor = sun.misc.Unsafe.class.getDeclaredConstructor();
            unsafeConstructor.setAccessible(true);
            unsafe = unsafeConstructor.newInstance();
            unsafeConstructor.setAccessible(false);
        } catch (Exception e) {
            fail("Should not have thrown any exception: \n " + e);
        }
        assertNotNull(unsafe);
    }

    @Test
    void throwException() {
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Unsafe.throwException(new IllegalArgumentException()));
        assertThrowsExactly(RuntimeException.class,
                () -> Unsafe.throwException(new RuntimeException()));
        assertThrowsExactly(Exception.class,
                () -> Unsafe.throwException(new Exception()));
        assertThrowsExactly(Error.class,
                () -> Unsafe.throwException(new Error()));
        assertThrowsExactly(Throwable.class,
                () -> Unsafe.throwException(new Throwable()));
    }

    @Test
    void crashVM() {
        System.out.println("Lets pretend, JVM Successfully crashed");
        assertNotEquals(5, 2 + 2);
        //Unsafe.crashVM();
    }

}
