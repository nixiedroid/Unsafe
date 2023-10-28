package com.nixiedroid.unsafe.samples.animal;

public class Lion {
    int age;
    long lenght_nm;

    public Lion(int age, long lenght_nm) {
        this.age = age;
        this.lenght_nm = lenght_nm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lion lion = (Lion) o;

        if (age != lion.age) return false;
        return lenght_nm == lion.lenght_nm;
    }

    @Override
    public int hashCode() {
        int result = age;
        result = 31 * result + (int) (lenght_nm ^ (lenght_nm >>> 32));
        return result;
    }
}
