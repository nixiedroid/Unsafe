package com.nixiedroid.unsafe;

import com.nixiedroid.unsafe.type.Size;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("unused")
public class Unsafe {
    private static final sun.misc.Unsafe theUnsafestThingyInJava;
    private static final ArrayList<Pointer> allocated;

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
        allocated = new ArrayList<>();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                int amount = 0;
                for (Pointer p : allocated) {
                    amount++;
                    Memory.free(p);
                }
                if (amount != 0) {
                    System.out.println("You are a terrible person");
                    System.out.println("Freed " + amount + " pointers");
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

    public static sun.misc.Unsafe getUnsafe() {
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

    public static int getPointerSize() {
        return getUnsafe().addressSize();
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
            allocated.add(p);
            return p;
        }

        public static void fill(Pointer p, int size, byte value) {
            getUnsafe().setMemory(p.address(), size, value);
        }

        public static void empty(Pointer p, int size) {
            fill(p, size, (byte) 0);
        }
        private static boolean removeFromAllocated(final Pointer pointer){
            for (int i = 0; i < allocated.size(); i++) {
                if (allocated.get(i) == pointer) {
                    allocated.remove(i);
                    return true;
                }
            }
            return false;
        }

        public static Pointer realloc(Pointer pointer, int bytes) {
            if (bytes <= 0) throw new IllegalArgumentException("Realloc size is wrong");
            Pointer.validate(pointer);
            if (removeFromAllocated(pointer)) {
                getUnsafe().freeMemory(pointer.address());
            } else {
                throw new IllegalArgumentException("Trying to reallocate dangling pointer");
            }
            Pointer p = new Pointer(getUnsafe().reallocateMemory(pointer.address(), bytes));
            allocated.add(p);
            return p;
        }

        @SuppressWarnings("SpellCheckingInspection")
        public static synchronized void free(Pointer pointer) {
            Pointer.validate(pointer);
            if (allocated.remove(pointer)) {
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

        /**
         * Reflection proof private constructor
         */
        private Objects() {
            Util.throwUtilityClassException();
        }

        /**
         * @param arrayClass Class with array
         * @return offset of array of {@param arrayClass}
         */
        public static int getArrayOffset(Class<?> arrayClass) {
            return getUnsafe().arrayBaseOffset(arrayClass);
        }

        /**
         * @param <T> clazz
         *            Allocates an instance but does not run any constructor.
         *            Initializes the class if it has not yet been.
         * @return instance of class {@link T}
         */
        public static <T> T createDummyInstance(Class<T> clazz) throws InstantiationException {
            return clazz.cast(getUnsafe().allocateInstance(clazz));
        }

        public static long sizeOf(Class<?> c) {
            long maximumOffset = 0;
            HashSet<Field> fields = new HashSet<>();
            while (c != Object.class) {
                Field[] df = c.getDeclaredFields();
                for (Field f : df) {
                    if ((f.getModifiers() & Modifier.STATIC) == 0) {
                        fields.add(f);
                    }
                }
                c = c.getSuperclass();
            }

            for (Field f : fields) {
                long offset = getUnsafe().objectFieldOffset(f);
                if (offset > maximumOffset) {
                    maximumOffset = offset;
                }
            }
            //return maximumOffset + 8;
            return ((maximumOffset / 8) + 1) * 8;   // padding
        }

        public static long sizeOf(Object o) {
            return sizeOf(o.getClass());
            //return ((maximumOffset/8) + 1) * 8;   // padding
        }

        public static void place(Object o, long address) throws Exception {
            Class<?> clazz = o.getClass();
            do {
                for (Field f : clazz.getDeclaredFields()) {
                    if (!Modifier.isStatic(f.getModifiers())) {
                        long offset = getUnsafe().objectFieldOffset(f);
                        if (f.getType() == long.class) {
                            getUnsafe().putLong(address + offset, getUnsafe().getLong(o, offset));
                        } else if (f.getType() == int.class) {
                            getUnsafe().putInt(address + offset, getUnsafe().getInt(o, offset));

                        }  else {
                            throw new UnsupportedOperationException("p " + f.getType());
                        }
                    }
                }
            } while ((clazz = clazz.getSuperclass()) != null);
        }

        public static Object read(Class<?> clazz, long address) throws Exception {
            Object instance = getUnsafe().allocateInstance(clazz);
            do {
                for (Field f : clazz.getDeclaredFields()) {
                    if (!Modifier.isStatic(f.getModifiers())) {
                        long offset = getUnsafe().objectFieldOffset(f);
                        if (f.getType() == long.class) {
                            getUnsafe().putLong(instance, offset, getUnsafe().getLong(address + offset));
                        } else if (f.getType() == int.class) {
                            getUnsafe().putLong(instance, offset, getUnsafe().getInt(address + offset));
                        } else {
                            throw new UnsupportedOperationException("r" + String.valueOf(f.getType()));
                        }
                    }
                }
            } while ((clazz = clazz.getSuperclass()) != null);
            return instance;
        }

        public static class Setters {
            public static void set(Object o, String fieldName, int value) throws NoSuchFieldException {
                Field f = o.getClass().getDeclaredField(fieldName);
                getUnsafe().putInt(o, getUnsafe().objectFieldOffset(f), value);
            }

            public static void set(Object o, String fieldName, long value) throws NoSuchFieldException {
                Field f = o.getClass().getDeclaredField(fieldName);
                getUnsafe().putLong(o, getUnsafe().objectFieldOffset(f), value);
            }


            public static void set(Object o, String fieldName, byte value) throws NoSuchFieldException {
                Field f = o.getClass().getDeclaredField(fieldName);
                getUnsafe().putByte(o, getUnsafe().objectFieldOffset(f), value);
            }

            public static void set(Object o, String fieldName, char value) throws NoSuchFieldException {
                Field f = o.getClass().getDeclaredField(fieldName);
                getUnsafe().putChar(o, getUnsafe().objectFieldOffset(f), value);
            }

            public static void set(Object o, String fieldName, short value) throws NoSuchFieldException {
                Field f = o.getClass().getDeclaredField(fieldName);
                getUnsafe().putShort(o, getUnsafe().objectFieldOffset(f), value);
            }

            public static void set(Object o, String fieldName, Object value) throws NoSuchFieldException {
                Field f = o.getClass().getDeclaredField(fieldName);
                getUnsafe().putObject(o, getUnsafe().objectFieldOffset(f), value);
            }
        }

        public static class Getters {

        }

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
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return "Pointer{" +
                    "address=" + address +
                    '}';
        }
    }

    private static final class AllocatedChunk {
            Pointer p;
            Size s;

        AllocatedChunk(Pointer p, Size s) {
            this.p = p;
            this.s = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AllocatedChunk that = (AllocatedChunk) o;

            if (!java.util.Objects.equals(p, that.p)) return false;
            return java.util.Objects.equals(s, that.s);
        }

        @Override
        public int hashCode() {
            int result = p != null ? p.hashCode() : 0;
            result = 31 * result + (s != null ? s.hashCode() : 0);
            return result;
        }

    }

}
