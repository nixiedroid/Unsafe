package com.nixiedroid.unsafe.type;

import java.nio.ByteOrder;

public class ByteOrderInfo {
    /**
     * <pre>
     * 32-bit integer 0x12345678
     * would be stored in bytes as
     * 0x12 0x34 0x56 0x78 (Big endian)
     * 0x78 0x56 0x34 0x12 (Little endian)
     * Number is 43981 (0xABCD)
     * In BE it will be stored as ABCD
     * In LE as CDAB
     * Endiannes means location of THE HIGHEST byte (aka 12 in 1234)
     *
     * __MSB_________LSB
     * _0x12__34__56__78
     * </pre>
     */
    public static final ByteOrder byteOrder = ByteOrder.nativeOrder();
}
