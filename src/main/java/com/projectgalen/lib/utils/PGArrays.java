package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGArrays.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 02, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
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

import com.projectgalen.lib.utils.collections.CollectionItem;
import com.projectgalen.lib.utils.delegates.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings({ "unchecked", "unused" })
public final class PGArrays {
    private PGArrays() { }

    public static <T> T @NotNull [] append(T[] array, T obj) {
        T[] copy = Arrays.copyOf(array, array.length + 1);
        copy[array.length] = obj;
        return copy;
    }

    public static <T> boolean areEqual(T[] leftArray, T[] rightArray, @NotNull Equality<T> equality) {
        if(leftArray == rightArray) return true;
        if((leftArray == null) || (rightArray == null) || (leftArray.length != rightArray.length)) return false;
        for(int i = 0; i < leftArray.length; i++) if(!equality.areEqual(leftArray[i], rightArray[i])) return false;
        return true;
    }

    /**
     * Create a stream of {@link CollectionItem} from the elements of an array.
     *
     * @param array The elements of the array.
     *
     * @return A stream of {@link CollectionItem}.
     *
     * @deprecated Use {@link Streams#arrayStream(Object[])} instead.
     */
    @SafeVarargs
    @Deprecated(forRemoval = true)
    public static <T> @NotNull Stream<CollectionItem<T>> arrayStream(T @NotNull ... array) {
        return Streams.arrayStream(array);
    }

    /**
     * Quick way to wrap elements in an array.
     *
     * @param elements The elements to return as an array.
     *
     * @return An array of the elements
     *
     * @deprecated Use {@link #wrap(Object[])} instead.
     */
    @SafeVarargs
    @Deprecated(forRemoval = true)
    public static <T> T[] asArray(T... elements) {
        return elements;
    }

    @Contract(pure = true)
    public static boolean compareCharArrays(char[] ch1, char[] ch2) {
        if((ch1 != null) && (ch2 != null) && (ch1.length == ch2.length)) {
            for(int i = 0; i < ch1.length; i++) if(ch1[i] != ch2[i]) return false;
            return true;
        }
        return ((ch1 == null) && (ch2 == null));
    }

    public static <T> T @NotNull [] createAndFill(int length, @NotNull T value) {
        T[] array = (T[])Array.newInstance(value.getClass(), length);
        Arrays.fill(array, value);
        return array;
    }

    public static double @NotNull [] createAndFill(int length, double value) {
        double[] array = new double[length];
        Arrays.fill(array, value);
        return array;
    }

    public static float @NotNull [] createAndFill(int length, float value) {
        float[] array = new float[length];
        Arrays.fill(array, value);
        return array;
    }

    public static long @NotNull [] createAndFill(int length, long value) {
        long[] array = new long[length];
        Arrays.fill(array, value);
        return array;
    }

    public static int @NotNull [] createAndFill(int length, int value) {
        int[] array = new int[length];
        Arrays.fill(array, value);
        return array;
    }

    public static short @NotNull [] createAndFill(int length, short value) {
        short[] array = new short[length];
        Arrays.fill(array, value);
        return array;
    }

    public static byte @NotNull [] createAndFill(int length, byte value) {
        byte[] array = new byte[length];
        Arrays.fill(array, value);
        return array;
    }

    public static char @NotNull [] createAndFill(int length, char value) {
        char[] array = new char[length];
        Arrays.fill(array, value);
        return array;
    }

    public static boolean @NotNull [] createAndFill(int length, boolean value) {
        boolean[] array = new boolean[length];
        Arrays.fill(array, value);
        return array;
    }

    @Contract(pure = true)
    public static int @NotNull [] filter(int @NotNull [] array, @NotNull IntPredicate predicate) {
        int[] out = new int[array.length];
        int   j   = 0;
        for(int k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    @Contract(pure = true)
    public static long @NotNull [] filter(long @NotNull [] array, @NotNull LongPredicate predicate) {
        long[] out = new long[array.length];
        int    j   = 0;
        for(long k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    @Contract(pure = true)
    public static byte @NotNull [] filter(byte @NotNull [] array, @NotNull BytePredicate predicate) {
        byte[] out = new byte[array.length];
        int    j   = 0;
        for(byte k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    @Contract(pure = true)
    public static short @NotNull [] filter(short @NotNull [] array, @NotNull ShortPredicate predicate) {
        short[] out = new short[array.length];
        int     j   = 0;
        for(short k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    @Contract(pure = true)
    public static char @NotNull [] filter(char @NotNull [] array, @NotNull CharPredicate predicate) {
        char[] out = new char[array.length];
        int    j   = 0;
        for(char k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    @Contract(pure = true)
    public static double @NotNull [] filter(double @NotNull [] array, @NotNull DoublePredicate predicate) {
        double[] out = new double[array.length];
        int      j   = 0;
        for(double k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    @Contract(pure = true)
    public static float @NotNull [] filter(float @NotNull [] array, @NotNull FloatPredicate predicate) {
        float[] out = new float[array.length];
        int     j   = 0;
        for(float k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    @Contract(pure = true)
    public static <T> T @NotNull [] filter(T @NotNull [] array, @NotNull Predicate<T> predicate) {
        T[] out = (T[])Array.newInstance(array.getClass().getComponentType(), array.length);
        int j   = 0;
        for(T k : array) if(predicate.test(k)) out[j++] = k;
        return Arrays.copyOf(out, j);
    }

    public static <T> T getFromArray(T[] array, int index, T defaultValue) {
        return (((array != null) && (array.length > index)) ? array[index] : defaultValue);
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> int getIndex(@Nullable T item, T @NotNull ... items) {
        for(int i = 0; i < items.length; i++) if(Objects.equals(item, items[i])) return i;
        return -1;
    }

    public static <T> T @NotNull [] join(T @NotNull [] array1, T @NotNull [] array2) {
        T[] copy = newArray(array1.getClass(), (array1.length + array2.length));
        System.arraycopy(array1, 0, copy, 0, array1.length);
        System.arraycopy(array2, 0, copy, array1.length, array2.length);
        return copy;
    }

    public static <U, T> T @NotNull [] copyAs(@NotNull Class<? extends T[]> cls, U @NotNull ... array) {
        return Arrays.copyOf(array, array.length, cls);
    }

    public static <T> T[] newArray(Class<? extends Object[]> arrayType, int length) {
        //noinspection unchecked
        return ((arrayType == Object[].class) ? (T[])new Object[length] : (T[])Array.newInstance(arrayType.getComponentType(), length));
    }

    public static <T> T @NotNull [] prepend(T obj, T @NotNull [] array) {
        T[] copy = newArray(array.getClass(), (array.length + 1));
        System.arraycopy(array, 0, copy, 1, array.length);
        copy[0] = obj;
        return copy;
    }

    public static char[] tr(char[] chars) {
        if((chars == null) || (chars.length == 0)) return chars;
        int[] range = tr1(chars);
        if(range[1] == 0) return new char[0];
        if((range[0] == 0) && (range[1] == chars.length)) return chars;
        return Arrays.copyOfRange(chars, range[0], range[1]);
    }

    /**
     * Quick way to wrap elements in an array.
     *
     * @param elements The elements to return as an array.
     *
     * @return An array of the elements.
     */
    @SafeVarargs
    public static <T> T[] wrap(T... elements) { return elements; }

    /**
     * Quick way to wrap characters in an array.
     *
     * @param characters The characters to return as an array.
     *
     * @return An array of the characters.
     */
    public static char[] wrap(char... characters) { return characters; }

    /**
     * Quick way to wrap integer numbers in an array.
     *
     * @param numbers The integer numbers to return as an array.
     *
     * @return An array of the integer numbers.
     */
    public static int[] wrap(int... numbers) { return numbers; }

    /**
     * Quick way to wrap bytes in an array.
     *
     * @param numbers The bytes to return as an array.
     *
     * @return An array of the bytes.
     */
    public static byte[] wrap(byte... numbers) { return numbers; }

    /**
     * Quick way to wrap short integer numbers in an array.
     *
     * @param numbers The short integer numbers to return as an array.
     *
     * @return An array of the short integer numbers.
     */
    public static short[] wrap(short... numbers) { return numbers; }

    /**
     * Quick way to wrap long integer numbers in an array.
     *
     * @param numbers The long integer numbers to return as an array.
     *
     * @return An array of the long integer numbers.
     */
    public static long[] wrap(long... numbers) { return numbers; }

    /**
     * Quick way to wrap single precision floating point numbers in an array.
     *
     * @param numbers The single precision floating point numbers to return as an array.
     *
     * @return An array of the single precision floating point numbers.
     */
    public static float[] wrap(float... numbers) { return numbers; }

    /**
     * Quick way to wrap double precision floating point numbers in an array.
     *
     * @param numbers The double precision floating point numbers to return as an array.
     *
     * @return An array of the double precision floating point numbers.
     */
    public static double[] wrap(double... numbers) { return numbers; }

    /**
     * Quick way to wrap boolean values in an array.
     *
     * @param bools The boolean values to return as an array.
     *
     * @return An array of the boolean values.
     */
    public static boolean[] wrap(boolean... bools) { return bools; }

    public static boolean z(char[] chars) {
        return ((chars == null) || (chars.length == 0) || (tr1(chars)[1] == 0));
    }

    private static int @NotNull [] tr1(char @NotNull [] chars) {
        for(int i = 0; i < chars.length; ) {
            int[] cp = U.codePointAt(chars, i);
            if(!Character.isWhitespace(cp[0])) return tr2(chars, i);
            i = cp[1];
        }
        return new int[]{ 0, 0 };
    }

    private static int @NotNull [] tr2(char @NotNull [] chars, int i) {
        for(int j = chars.length; j > i; ) {
            int[] cp = U.codePointAt(chars, j, true);
            if(!Character.isWhitespace(cp[0])) return new int[]{ i, j };
            j = cp[1];
        }
        return new int[]{ i, (i + 1) };
    }
}
