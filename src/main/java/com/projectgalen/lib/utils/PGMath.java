package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGMath.java
//         IDE: IntelliJ
//      AUTHOR: Galen Rhodes
//        DATE: January 05, 2023
//
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;

@SuppressWarnings("unused")
public final class PGMath {
    private PGMath() { }

    @Contract("null -> null; !null -> !null")
    public static BigDecimal getBigDecimal(@Nullable Number num) {
        return ((num == null) ? null : ((num instanceof BigDecimal) ? (BigDecimal)num : ((num instanceof BigInteger) ? new BigDecimal((BigInteger)num) : BigDecimal.valueOf(num.doubleValue()))));
    }

    @Contract("null -> null; !null -> !null")
    public static BigInteger getBigInteger(@Nullable Number num) {
        return ((num == null) ? null : ((num instanceof BigInteger) ? (BigInteger)num : ((num instanceof BigDecimal) ? ((BigDecimal)num).toBigInteger() : BigInteger.valueOf(num.longValue()))));
    }

    /**
     * Compares the given value <code>v</code> to see if it is in the range defined by the two values <code>a</code> and <code>b</code>.
     *
     * @param v The value.
     * @param a The first value of the range.
     * @param b The second value of the range.
     * @return <code>true</code> if the value is in between values <code>a</code> and <code>b</code> inclusive.
     */
    public static boolean isInRangeInclusive(int v, int a, int b) {
        return (((a <= b) ? ((v >= a) && (v <= b)) : ((v >= b) && (v <= a))));
    }

    public static boolean mod(int a, int b) {
        return ((a % b) == 0);
    }

    @Contract(pure = true)
    public static char max(char first, char @NotNull ... values) {
        for(char v : values) if(v > first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static byte max(byte first, byte @NotNull ... values) {
        for(byte v : values) if(v > first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static short max(short first, short @NotNull ... values) {
        for(short v : values) if(v > first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static int max(int first, int @NotNull ... values) {
        for(int v : values) if(v > first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static long max(long first, long @NotNull ... values) {
        for(long v : values) if(v > first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static float max(float first, float @NotNull ... values) {
        for(float v : values) if(v > first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static double max(double first, double @NotNull ... values) {
        for(double v : values) if(v > first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static char min(char first, char @NotNull ... values) {
        for(char v : values) if(v < first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static byte min(byte first, byte @NotNull ... values) {
        for(byte v : values) if(v < first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static short min(short first, short @NotNull ... values) {
        for(short v : values) if(v < first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static int min(int first, int @NotNull ... values) {
        for(int v : values) if(v < first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static long min(long first, long @NotNull ... values) {
        for(long v : values) if(v < first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static float min(float first, float @NotNull ... values) {
        for(float v : values) if(v < first) first = v;
        return first;
    }

    @Contract(pure = true)
    public static double min(double first, double @NotNull ... values) {
        for(double v : values) if(v < first) first = v;
        return first;
    }
}
