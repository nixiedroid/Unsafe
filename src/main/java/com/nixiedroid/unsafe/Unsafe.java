package com.nixiedroid.unsafe;

import com.nixiedroid.unsafe.type.Pointer;
import com.nixiedroid.unsafe.type.Size;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Unsafe {
    private static final sun.misc.Unsafe theUnsafestThingyInJava;
    private static final Map<Pointer, Size> allocated;

    static {
        sun.misc.Unsafe unsafe;
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
        } catch (NoSuchFieldException e) {
            try {
                Constructor<sun.misc.Unsafe> unsafeConstructor = sun.misc.Unsafe.class.getDeclaredConstructor();
                unsafeConstructor.setAccessible(true);
                unsafe = unsafeConstructor.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(e + " : " + ex);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        theUnsafestThingyInJava = unsafe;
        allocated = new HashMap<>();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                int amount = 0;
                for (Map.Entry<Pointer, Size> entry : allocated.entrySet()) {
                    amount++;
                    size += entry.getValue().size();
                    Memory.calloc(entry.getKey());
                }
                if (amount != 0) {
                    System.out.println("You are a terrible person");
                    System.out.println("Freed " + amount + " pointers");
                    System.out.println("Saved " + size + " bytes from leaking");
                }
            }
        }));
    }

    private Unsafe() {
        Util.throwUtilityClassException();
    }

    private static sun.misc.Unsafe getUnsafe() {
        return theUnsafestThingyInJava;
    }

    /**
     * crash java VM, showing new awesome type of Exception
     */
    public static void crashVM() {
        getUnsafe().getByte(0);
    }

    /**
     * throw Exception without requiring to add throws to class
     *
     * @param e Exception to be thrown
     */
    public static void throwException(Exception e) {
        getUnsafe().throwException(e);
    }

    public static int getPointerSize() {
        return getUnsafe().addressSize();
    }



    public static class Memory {
        private Memory() {
            Util.throwUtilityClassException();
        }

        public static Pointer malloc(int bytes) {
            if (bytes <= 0) throw new IllegalArgumentException("Malloc size is wrong");
            Pointer p = new Pointer(getUnsafe().allocateMemory(bytes));
            allocated.put(p, new Size(bytes));
            return p;
        }

        public static void fill(Pointer p, int size, byte value) {
            getUnsafe().setMemory(p.address(), size, value);
        }

        public static void empty(Pointer p, int size) {
            fill(p, size, (byte) 0);
        }

        public static Pointer realloc(Pointer pointer, int bytes) {
            if (bytes <= 0) throw new IllegalArgumentException("Realloc size is wrong");
            Pointer.validate(pointer);
            if (allocated.remove(pointer) != null) {
                getUnsafe().freeMemory(pointer.address());
            } else {
                throw new IllegalArgumentException("Trying to reallocate dangling pointer");
            }
            if (allocated.get(pointer).size() >= bytes) {
                throw new IllegalArgumentException("Trying to shrink allocated size");
            }
            Pointer p = new Pointer(getUnsafe().reallocateMemory(pointer.address(), bytes));
            allocated.put(p, new Size(bytes));
            return p;
        }

        @SuppressWarnings("SpellCheckingInspection")
        public static synchronized void calloc(Pointer pointer) {
            Pointer.validate(pointer);
            if (allocated.remove(pointer) != null) {
                getUnsafe().freeMemory(pointer.address());
            } else {
                throw new IllegalArgumentException("Trying to free dangling pointer");
            }
        }

        /**
         * Perfomance equals to System.arraycopy
         *
         * @param scr  Pointer to beginning of source array
         * @param dst  Pointer to beginning of destination array
         * @param size size in bytes
         * @see System#arraycopy(Object, int, Object, int, int)
         */

        @SuppressWarnings("SpellCheckingInspection")
        public static void memcpy(Pointer scr, Pointer dst, long size) {
            Pointer.validate(scr);
            Pointer.validate(dst);
            getUnsafe().copyMemory(scr.address(), dst.address(), size);

        }

        @SuppressWarnings("SpellCheckingInspection")
        public static void memcpy(Pointer scr, int offsetSrc, Pointer dst, int offsetDst, long size) {
            Pointer.validate(scr);
            Pointer.validate(dst);
            getUnsafe().copyMemory(
                    scr.address() + offsetSrc,
                    dst.address() + offsetDst,
                    size);
        }

        public static byte getByte(Pointer pointer) {
            Pointer.validate(pointer);
            return getUnsafe().getByte(pointer.address());
        }

        public static byte getByte(Pointer pointer, int offset) {
            Pointer.validate(pointer);
            return getUnsafe().getByte(pointer.address() + offset);
        }

        public static void setByte(Pointer pointer, byte value) {
            Pointer.validate(pointer);
            getUnsafe().putByte(pointer.address(), value);
        }

        public static void setByte(Pointer pointer, int offset, byte value) {
            Pointer.validate(pointer);
            getUnsafe().putByte(pointer.address() + offset, value);
        }
    }


    public static class Objects {
        private Objects() {
            Util.throwUtilityClassException();
        }
        public static int getArrayOffset(Class<?> arrayClass) {
            return getUnsafe().arrayBaseOffset(arrayClass);
        }

        /**
         * @param <T> clazz
         * Allocates an instance but does not run any constructor.
         * Initializes the class if it has not yet been.
         * @return instance of class {@link T}
         */
        public static <T> T createDummyInstance(Class<T> clazz) throws InstantiationException {
           return clazz.cast(getUnsafe().allocateInstance(clazz));
        }

    }

}
