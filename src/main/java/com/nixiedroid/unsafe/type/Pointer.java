package com.nixiedroid.unsafe.type;

/**
 * C Pointer. Nothing interesting. Go away
 */
public class Pointer {
    private final long address;
    public Pointer(long address) {
        if (address == 0) throw new NullPointerException("Pointer is ACTUALLY null");
        this.address = address;
    }

    public long address() {
        return address;
    }

    public static void validate(Pointer p) {
        if (p == null) throw new IllegalArgumentException("Pointer is null");
        if (p.address() == 0) throw new IllegalArgumentException("Pointer address is null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pointer pointer = (Pointer) o;

        return address == pointer.address;
    }

    @Override
    public int hashCode() {
        return (int) (address ^ (address >>> 32));
    }
}
