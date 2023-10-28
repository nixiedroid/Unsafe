package unsafe;

import com.nixiedroid.unsafe.Unsafe;
import com.nixiedroid.unsafe.samples.animal.Lion;
import org.junit.jupiter.api.Test;
import com.nixiedroid.unsafe.samples.Person;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class UnsafeObjectsTest {
    private  void createUtilityObjectReflection() {
        try {
            Constructor<Unsafe.Objects> constructor = ( Unsafe.Objects.class).getDeclaredConstructor();
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
    public void createInstanceUsingDefaultConstructor() {
        Person p = new Person(25, "Smith");
        assertNotNull(p);
        assertEquals("Tom", p.name);
        assertEquals("Smith", p.surname);
        assertEquals(25, p.age);
        assertEquals(2, p.amountOfChildren);
        assertEquals(3, p.amountOfDogs);
        assertEquals(4, p.amountOfCats);
        assertTrue(p.isLikesDogs);
        assertTrue(p.isLikesCats);
        assertEquals(96, Person.somethingUndiscovered);
        assertEquals(34, p.getSum());
    }

    @Test
    public void createInstanceUsingReflection() {
        Person p = null;
        try {
            Constructor<Person> constructor = Person.class.getDeclaredConstructor(int.class, String.class);
            p = constructor.newInstance(25, "Smith");
        } catch (Exception e) {
            fail("Should not have thrown any exception: \n " + e);
        }
        assertNotNull(p);
        assertEquals("Tom", p.name);
        assertEquals("Smith", p.surname);
        assertEquals(25, p.age);
        assertEquals(2, p.amountOfChildren);
        assertEquals(3, p.amountOfDogs);
        assertEquals(4, p.amountOfCats);
        assertTrue(p.isLikesDogs);
        assertTrue(p.isLikesCats);
        assertEquals(96, Person.somethingUndiscovered);
        assertEquals(34, p.getSum());
    }

    @Test
    void serialiseTest(){
        int containerSize = (int) Unsafe.Objects.sizeOf(Lion.class);
        Unsafe.Pointer address = Unsafe.Memory.malloc(containerSize);
        Lion c1 = new Lion(10, 10000L);
        Lion c2 = new Lion(5, 1254L);
        Lion newC1 = null;
        Lion newC2 = null;
        try {
            Unsafe.Objects.place(c1, address.address());
            Unsafe.Objects.place(c2, address.address() + containerSize);

           newC1 = (Lion) Unsafe.Objects.read(Lion.class, address.address());
           newC2 = (Lion) Unsafe.Objects.read(Lion.class, address.address() + containerSize);
        } catch (Exception e) {
            fail("Should not have thrown any exception: \n " + e);
        }
        assertNotNull(newC1);
        assertNotNull(newC2);
        assertEquals(c1, newC1);
        assertEquals(c2, newC2);
    }

    @Test
    public void createInstanceWithoutConstructor() {
        Person p = null;
        try {
            p = Unsafe.Objects.createDummyInstance(Person.class);
        } catch (Exception e) {
            fail("Should not have thrown any exception : \n " + e);
        }
        assertNotNull(p);
        assertEquals("Tom", p.name); //THIS
        assertNull(p.surname);
        assertEquals(0, p.age);
        assertEquals(2, p.amountOfChildren);  //THIS
        assertEquals(0, p.amountOfDogs);
        assertEquals(0, p.amountOfCats);
        assertTrue(p.isLikesDogs);  //THIS
        assertFalse(p.isLikesCats);
        assertEquals(96, Person.somethingUndiscovered); //THIS. Obviously
        assertEquals(2, p.getSum());
    }

    @Test
    public void sizeOf(){
        Person p = new Person(20,"gg");
        assertEquals(40,Unsafe.Objects.sizeOf(p));
        assertEquals(40,Unsafe.Objects.sizeOf(p));
    }


}
