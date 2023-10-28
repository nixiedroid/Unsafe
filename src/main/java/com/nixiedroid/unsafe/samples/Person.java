package com.nixiedroid.unsafe.samples;

public class Person {
    public static int somethingUndiscovered = 96;
    public final String name = "Tom";
    public final int amountOfChildren = 2;
    public final boolean isLikesDogs = true;
    public String surname;
    public int age;
    public int amountOfDogs = 3;
    public int amountOfCats;
    public boolean isLikesCats = true;

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
