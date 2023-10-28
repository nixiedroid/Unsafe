package unsafe;

import com.nixiedroid.unsafe.Unsafe.Pointer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PointerTest {

    private Pointer getPointer(long address)  {
        Pointer p = null;
        try {
            Constructor<Pointer> pointerConstructor = Pointer.class.getDeclaredConstructor(long.class);
            pointerConstructor.setAccessible(true);
            p = pointerConstructor.newInstance(address);
            pointerConstructor.setAccessible(false);
        } catch (InvocationTargetException e){
            throw (NullPointerException) e.getTargetException();
           // throwException((Exception) e.getTargetException());
        } catch (Exception e) {
            fail("Should not have thrown any exception : \n " + e);
        }
        return p;
    }
    private Pointer getZeroPointer(){
        Pointer p = null;
        try {
            Constructor<Pointer> constructor = Pointer.class.getDeclaredConstructor(long.class);
            constructor.setAccessible(true);
            p = constructor.newInstance(1);
            constructor.setAccessible(false);
            Field address =  p.getClass().getDeclaredField("address");
            address.setAccessible(true);
            address.set(p,0);
            address.setAccessible(false);
        } catch (Exception e) {
            fail("Should not have thrown any exception : \n " + e);
        }
        return p;
    }
    
    @Test
    void address() {
        Pointer p = getPointer(20);
        assertEquals(20,p.address());
        assertThrows(NullPointerException.class,()->getPointer(0));
        assertDoesNotThrow(()->getPointer(-1));
    }

    @Test
    void validate() {
        Pointer p = getZeroPointer();
        assertThrows(IllegalArgumentException.class,()->Pointer.validate(p));
        assertThrows(IllegalArgumentException.class,()-> Pointer.validate(null));
        assertThrows(NullPointerException.class,()-> Pointer.validate(getPointer(0)));
    }

    @Test
    void testEquals() {
        Pointer first = getPointer(42);
        Pointer second = getPointer(42);
        Pointer notEquals = getPointer(43);
        assertNotEquals(first, second);
        assertNotEquals(first, notEquals);
        assertNotEquals(first,null);
        assertNotEquals(first,"null");
        assertNotEquals(0x1234_5678_9ABC_D0EFL,0x9ABC_D0EF_1234_5678L);
        assertNotEquals(getPointer(0x1234_5678_9ABC_D0EFL),getPointer(0x9ABC_D0EF_1234_5678L));
        assertNotEquals(first,getPointer(42));
        Pointer p = getPointer(25);
        Pointer pClone = p;
        Pointer doubleClone = pClone;
        assertEquals(p,pClone);
        assertEquals(doubleClone,pClone);
        assertEquals(p,doubleClone);
    }

    @Test
    void testHashCode() {
        assertNotEquals(getPointer(1).hashCode(),getPointer(1).hashCode());
        assertNotEquals(getPointer(0x1234_5678_9ABC_D0EFL).hashCode(),getPointer(0x9ABC_D0EF_1234_5678L).hashCode());
        Random random = new Random();
        int halfFirst = random.nextInt();
        int halfSecond = random.nextInt();
        long firstAddress = halfFirst + ((long) halfSecond << 32);
        long collidingAddress = halfSecond+ ((long) halfFirst << 32);
        assertNotEquals(firstAddress,collidingAddress);
        assertNotEquals(getPointer(firstAddress).hashCode(),getPointer(collidingAddress).hashCode());
        Pointer p = getPointer(25);
        Pointer pClone = p;
        Pointer doubleClone = pClone;
        assertEquals(p.hashCode(),pClone.hashCode());
        assertEquals(doubleClone.hashCode(),pClone.hashCode());
        assertEquals(p.hashCode(),doubleClone.hashCode());
    }

    @Test
    void testToString() {
    }
}