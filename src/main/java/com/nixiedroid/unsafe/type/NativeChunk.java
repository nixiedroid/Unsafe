package com.nixiedroid.unsafe.type;

import com.nixiedroid.unsafe.Unsafe;

public class NativeChunk {
    private final int size;
    private final Pointer address;
    private boolean isFreed = false;

    public NativeChunk(int size) {
        if (size <=0 ) throw new IllegalArgumentException("Array size should be greater than 0");
        this.size = size;
        address = Unsafe.Memory.malloc(size);
        Unsafe.Memory.empty(address,size);
    }

    public int size() {
        return size;
    }

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

    //    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//        System.out.println("Garbage collecting bytearray " + this.size);
//        if (!isFreed){
//            isFreed = true;
//            UnsafeWrapper.calloc(address);
//        }
//    }
    @SuppressWarnings("CommentedOutCode")
    public void free() {
        if (isFreed) {
            throw new UnsupportedOperationException("Trying to free already freed bytearray");
        }
        isFreed = true;
        Unsafe.Memory.calloc(address);
    }
    public static class Window{
        private Window(){
            throw new UnsupportedOperationException("You shall not create instance of this");
        }


    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Native Chunk: [");
        for (int i = 0; i < size-1; i++) {
            sb.append(  String.format("%02x", get(i) & 0xFF));
            sb.append(",");
        }
        sb.append(  String.format("%02x", get(size-1) & 0xFF));
        return sb.append("]").toString();
    }
}
