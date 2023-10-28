package unsafe;

import com.nixiedroid.unsafe.Unsafe;
import com.nixiedroid.unsafe.samples.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class UnsafeObjectsTest {
    private  void createUtilityObjectReflection() {
        try {
            Constructor<Unsafe> constructor = ( Unsafe.class).getDeclaredConstructor();
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
    public void createInstanceWithoutConstructor() {
        Person p = null;
        try {
            p = Unsafe.createDummyInstance(Person.class);
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



}
