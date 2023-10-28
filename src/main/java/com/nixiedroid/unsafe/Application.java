package com.nixiedroid.unsafe;

public class Application {

    public static void main(String[] args) {
       Person p = new Person(25,"Tom");
       long objPo;
        System.out.println(Unsafe.Objects.sizeOf(p));
        System.out.println(Unsafe.Objects.sizeOf(Person.class));
        System.out.println(Unsafe.Objects.sizeOf(p.getClass()));
    }
    static class Person {
        static int somethingUndiscovered = 96;
        final String name = "Tom";
        final int amountOfChildren = 2;
        final boolean isLikesDogs = true;
        String surname;
        int age;
        int amountOfDogs = 3;
        int amountOfCats;
        boolean isLikesCats = true;

        {
            amountOfCats = 4;
        }

        public Person(int age, String surname) {
            this.age = age;
            this.surname = surname;
        }
        public int getSum() {
            return age + amountOfCats + amountOfDogs + amountOfChildren;
        }
    }

}
