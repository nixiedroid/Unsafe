package com.nixiedroid.unsafe.type;

import com.nixiedroid.unsafe.Unsafe;
import com.nixiedroid.unsafe.Unsafe.Pointer;
import com.nixiedroid.unsafe.Util;

public final class NativeChunk {
    private final int size;
    private final Pointer address;
    private boolean isFreed = false;

    public NativeChunk(int size) {
        if (size <= 0) throw new IllegalArgumentException("Array size should be greater than 0");
        this.size = size;
        address = Unsafe.Memory.malloc(size);
        Unsafe.Memory.empty(address, size);
    }

    public int size() {
        return size;
    }

    /**
     * Sets byte value of {@param value} to native array offset of {@param offset}
     * @param offset Offset in bytes from pointer to array
     * @param value byte value
     */
    public void set(int offset, byte value) {
        if (offset >= size) {
            throw new IndexOutOfBoundsException("Offset " + offset + " is bigger than size " + size);
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException("Offset " + offset + " is lower than 0 ");
        }
        if (isFreed) {
            throw new UnsupportedOperationException("Trying to use already freed bytearray");
        }
        Unsafe.Memory.setByte(address, offset, value);

    }
    /**
     * Gets byte value of native array offset {@param offset}
     * @param offset Offset in bytes from pointer to array
     */

    public byte get(int offset) {
        if (offset >= size) {
            throw new IndexOutOfBoundsException("Offset " + offset + " is bigger than size " + size);
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException("Offset " + offset + " is lower than 0 ");
        }
        if (isFreed) {
            throw new UnsupportedOperationException("Trying to use already freed bytearray");
        }
        return Unsafe.Memory.getByte(address, offset);
    }
    /**
     * <pre>
     * Deallocates byte array
     *
     * WARNING. MUST BE CALLED AFTER END OF USAGE
     * GARBAGE COLLECTOR USELESS HERE
     * </pre>
     */

    public void free() {
        if (isFreed) {
            throw new UnsupportedOperationException("Trying to free already freed bytearray");
        }
        isFreed = true;
        Unsafe.Memory.free(address);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Native Chunk: [");
        for (int i = 0; i < size - 1; i++) {
            sb.append(String.format("%02x", get(i) & 0xFF));
            sb.append(",");
        }
        sb.append(String.format("%02x", get(size - 1) & 0xFF));
        return sb.append("]").toString();
    }

    public static final class Window {
        /**
         * Reflection proof private constructor
         */
        private Window() {
            Util.throwUtilityClassException();
        }


    }
}
