package com.nixiedroid.unsafe.type;

public final class Size {
    private final int size;

    public Size(int size) {
        if (size<0) throw new IllegalArgumentException("Size must be greater than 0");
        this.size = size;
    }

    public int size() {
        return size;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size size1 = (Size) o;

        return size == size1.size;
    }

    @Override
    public int hashCode() {
        return size;
    }

    @Override
    public String toString() {
        return "Size{" +
                "size=" + size +
                '}';
    }
}
