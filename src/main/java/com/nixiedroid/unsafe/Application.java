package com.nixiedroid.unsafe;

import com.nixiedroid.unsafe.type.NativeChunk;

public class Application {

    public static void main(String[] args) {
        System.out.println("Pointer size = " + Unsafe.getPointerSize());
        NativeChunk array = new NativeChunk(Integer.MAX_VALUE);
        array.set(0, (byte) 0x5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(Util.toString(array.get(i)));
            sb.append(" - ");
            sb.append(i);
            sb.append("\n");
        }
        System.out.println(sb);
        Util.halt(10000);
        array.free();
    }

}
