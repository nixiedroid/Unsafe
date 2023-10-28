package com.nixiedroid.unsafe;

import com.nixiedroid.unsafe.samples.Person;
import com.nixiedroid.unsafe.type.ByteOrderInfo;

public class Application {
    static Person d = new Person(25,"Tom");

    public static void main(String[] args) {
        System.out.println(ByteOrderInfo.byteOrder);
        Person p1 = new Person(25,"top");
        Person p2 = new Person(24,"tom");
        Person[] ap = new Person[]{p1,p2};
        System.out.println(Unsafe.getUnsafe().arrayBaseOffset(ap.getClass()));
        System.out.println(Unsafe.getUnsafe().arrayIndexScale(ap.getClass()));
        showObject(ap);


    }
    public static void showObject(Object o){
        System.out.println("NUM HEX - VAL");
        String binString;
        Person p = new Person(25,"Tom");
        System.out.println(Unsafe.Objects.sizeOf(d));
        int size = (int) Unsafe.Objects.sizeOf(o);
        byte[] bytes = new byte[4];

        for (int i = 0; i < size; i+=1) {
            int val = (bytes[0] & 0xFF) | (bytes[1]  & 0xFF) << 8 |
                    (bytes[2]  & 0xFF) << 16 | (bytes[3]  & 0xFF) << 24;
            binString = String.format("%32s", Integer.toBinaryString(val)).replace(' ', '0');
            System.out.printf("%02d:  %08X %s",i,val, binString);
            Util.print(bytes);
            System.out.println();
        }
    }
}
