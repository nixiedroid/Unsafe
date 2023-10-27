import com.nixiedroid.unsafe.Unsafe;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnsafeObjectsTest {
    @Test
    public void createInstanceWithoutConstructor() {
        Person p = new Person(25,"Smith");
        assertNotNull(p);
        assertEquals("Tom",p.name);
        assertEquals("Smith",p.surname);
        assertEquals(25,p.age);
        assertEquals(2,p.amountOfChildren);
        assertEquals(3,p.amountOfDogs);
        assertEquals(4,p.amountOfCats);
        assertTrue(p.isLikesDogs);
        assertTrue(p.isLikesCats);
        assertEquals(96,Person.somethingUndiscovered);
        Person torturedPerson;
        try {
            torturedPerson = Unsafe.Objects.createDummyInstance(Person.class);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        assertEquals("Tom",torturedPerson.name); //THIS
        assertNull(torturedPerson.surname);
        assertEquals(0,torturedPerson.age);
        assertEquals(2,torturedPerson.amountOfChildren);  //THIS
        assertEquals(0,torturedPerson.amountOfDogs);
        assertEquals(0,torturedPerson.amountOfCats);
        assertTrue(torturedPerson.isLikesDogs);  //THIS
        assertFalse(torturedPerson.isLikesCats);
        assertEquals(96,Person.somethingUndiscovered);
    }

    public static class Person {
        final String name = "Tom";
        String surname;
        int age;
        final int amountOfChildren = 2;
        int amountOfDogs = 3;
        int amountOfCats;
        {
            amountOfCats = 4;
        }
        boolean isLikesCats = true;
        final boolean isLikesDogs = true;
        static int somethingUndiscovered = 96;
        public Person(int age, String surname) {
            this.age = age;
            this.surname = surname;
        }
    }

}
