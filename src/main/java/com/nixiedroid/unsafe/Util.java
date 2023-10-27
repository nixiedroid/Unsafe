package com.nixiedroid.unsafe;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class Util {
    private Util() {
        throwUtilityClassException();
    }

    @SuppressWarnings({"InfiniteLoopStatement", "StatementWithEmptyBody"})
    public static void halt() {
        while (true) ;
    }

    public static void halt(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    @SuppressWarnings("UnusedAssignment")
    public static synchronized void performGc() {
        new Thread(() -> {
            Object o = new Object();
            WeakReference<Object> ref = new WeakReference<>(o);
            o = null;
            while (ref.get() != null) {
                System.gc();
            }
            System.out.println("Garbage collector finished");
        }).start();
    }

    public static void print(byte data) {
        System.out.printf("%02x", data & 0xFF);
    }

    public static String toString(byte data) {
        return String.format("%02x", data & 0xFF);
    }

    public static void print(byte[] data) {
        System.out.print("Hex value: ");
        for (byte b : data) {
            System.out.printf("%02x", b & 0xFF);
        }
        System.out.println();
    }

    public static String toString(byte[] data) {
        StringBuilder out = new StringBuilder();
        for (byte b : data) {
            out.append(String.format("%02x", b & 0xFF));
        }
        return out + "\n";
    }

    public static void throwUtilityClassException() {
        throw new UnsupportedOperationException("Unable to create instance of utility class");
    }


//    private static void suppressException() {
//        try {
//            throw new RuntimeException();
//        } finally {
//            return;
//        }
//    }
}
