package com.nixiedroid.unsafe;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class Util {
    /**
     * Reflection proof private constructor
     */
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
    private static void performGcHalting(){
        Object o = new Object();
        WeakReference<Object> ref = new WeakReference<>(o);
        o = null;
        while (ref.get() != null) {
            System.gc();
        }
    }


    public static synchronized void performGc() {
        new Thread(Util::performGcHalting).start();
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
        for (int i = 0; i < data.length-1; i++) {
            out.append(String.format("%02x", data[i] & 0xFF));
            out.append(", ");
        }
        out.append(String.format("%02x", data[data.length-1] & 0xFF));
        return out.toString();
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
