package com.nixiedroid.unsafe;

import com.nixiedroid.unsafe.type.Size;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Unsafe {
    private static final sun.misc.Unsafe theUnsafestThingyInJava;
    private static final HashMap<Pointer,Size> allocated;

    static {
        sun.misc.Unsafe unsafe;
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
            field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            try {
                Constructor<sun.misc.Unsafe> unsafeConstructor = sun.misc.Unsafe.class.getDeclaredConstructor();
                unsafeConstructor.setAccessible(true);
                unsafe = unsafeConstructor.newInstance();
                unsafeConstructor.setAccessible(false);
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
                int amount = 0;
                int size =0;
                for (Map.Entry<Pointer,Size> entry : allocated.entrySet()) {
                    size+=entry.getValue().size();
                    amount++;
                    Memory.free(entry.getKey());
                }
                if (amount != 0) {
                    System.out.println("You are a terrible person");
                    System.out.println("Freed " + amount + " pointers");
                    System.out.println("Equally  " + size + " bytes");
                }
            }
        }));
    }

    /**
     * Reflection proof private constructor
     */
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
    public static void throwException(Throwable e) {
        getUnsafe().throwException(e);
    }


    public static class Memory {
        /**
         * Reflection proof private constructor
         */
        private Memory() {
            Util.throwUtilityClassException();
        }

        public static Pointer malloc(int bytes) {
            if (bytes <= 0) throw new IllegalArgumentException("Malloc size is wrong");
            Pointer p = new Pointer(getUnsafe().allocateMemory(bytes));
            allocated.put(p,new Size(bytes));
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
            if (allocated.containsKey(pointer)) {
                if (allocated.get(pointer).size()<=bytes) throw new IllegalArgumentException("Shrinking memory size is not possible");
                getUnsafe().freeMemory(pointer.address());
            }
            else {
                throw new IllegalArgumentException("Trying to reallocate dangling pointer");
            }
            Pointer p = new Pointer(getUnsafe().reallocateMemory(pointer.address(), bytes));
            allocated.put(p,new Size(bytes));
            return p;
        }

        public static synchronized void free(Pointer pointer) {
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

    public static <T> T createDummyInstance(Class<T> clazz) throws InstantiationException {
        return clazz.cast(getUnsafe().allocateInstance(clazz));
    }

    public static final class Pointer {
        private final long address;

        private Pointer(long address) {
            if (address == 0) throw new NullPointerException("Pointer is ACTUALLY null");
            this.address = address;
        }

        public static void validate(Pointer p) {
            if (p == null) throw new IllegalArgumentException("Pointer is null");
            if (p.address() == 0) throw new IllegalArgumentException("Pointer address is null");
        }

        public long address() {
            return address;
        }

        @Override
        public String toString() {
            return "Pointer{" +
                    "address=" + address +
                    '}';
        }
    }

}
