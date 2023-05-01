package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: U.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: January 23, 2023
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

import com.projectgalen.lib.utils.delegates.GetWithValueDelegate;
import com.projectgalen.lib.utils.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

@SuppressWarnings({ "unused", "SameParameterValue" })
public final class U {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private U() { }

    public static <T> T @NotNull [] append(T[] array, T obj) {
        T[] copy = Arrays.copyOf(array, array.length + 1);
        copy[array.length] = obj;
        return copy;
    }

    public static @NotNull StringBuilder appendFormat(@NotNull StringBuilder sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
    }

    public static @NotNull StringBuffer appendFormat(@NotNull StringBuffer sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
    }

    /**
     * Quick way to wrap elements in an array.
     *
     * @param elements The elements to return as an array.
     * @return An array of the elements
     * @deprecated Use {@link #wrap(Object[])} instead.
     */
    @SafeVarargs
    @Deprecated(forRemoval = true)
    public static <T> T[] asArray(T... elements) {
        return elements;
    }

    public static <T1, T2> @NotNull Map<T1, T2> asMap(Class<? extends Map<T1, T2>> cls, T1 @NotNull [] keys, T2 @NotNull [] values) {
        try {
            if(keys.length != values.length) throw new IllegalAccessException(msgs.format("msg.err.as_map.key_value_count_mismatch", keys.length, values.length));
            Map<T1, T2> map = cls.getConstructor().newInstance();
            for(int i = 0; i < keys.length; i++) map.put(keys[i], values[i]);
            return map;
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean atomicSet(@NotNull AtomicBoolean atomicBoolean, boolean value) {
        atomicBoolean.set(value);
        return value;
    }

    public static byte @NotNull [] base64Decode(@NotNull String encStr) {
        return Base64.getDecoder().decode(encStr);
    }

    public static @NotNull String base64Encode(byte @NotNull [] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static @NotNull String capitalize(@NotNull String str) {
        return ((str.length() == 0) ? str : ((str.length() == 1) ? str.toUpperCase() : (str.substring(0, 1).toUpperCase() + str.substring(1))));
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static int @NotNull [] codePointAt(char @NotNull [] chars, int idx, boolean backwards) {
        if(!backwards) return codePointAt(chars, idx);
        if(idx < 1 || idx > chars.length) return new int[] { -1, idx };
        char c2 = chars[--idx];
        if(Character.isLowSurrogate(c2) && (idx > 0)) {
            char c1 = chars[idx - 1];
            if(Character.isHighSurrogate(c1)) return new int[] { Character.toCodePoint(c1, c2), (idx - 1) };
        }
        return new int[] { c2, idx };
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static int @NotNull [] codePointAt(char @NotNull [] chars, int idx) {
        if(idx < 0 || idx >= chars.length) return new int[] { -1, idx };
        char c1 = chars[idx++];
        if(Character.isHighSurrogate(c1) && (idx < chars.length)) {
            char c2 = chars[idx];
            if(Character.isLowSurrogate(c2)) return new int[] { Character.toCodePoint(c1, c2), (idx + 1) };
        }
        return new int[] { c1, idx };
    }

    @Contract("_, _ -> param1")
    public static @NotNull StringBuffer concat(@NotNull StringBuffer sb, Object @NotNull ... args) {
        for(Object o : args) sb.append(o);
        return sb;
    }

    @Contract("_, _ -> param1")
    public static @NotNull StringBuilder concat(@NotNull StringBuilder sb, Object @NotNull ... args) {
        for(Object o : args) sb.append(o);
        return sb;
    }

    public static @NotNull String concat(Object... args) {
        return concat(new StringBuilder(), args).toString();
    }

    @SuppressWarnings("unchecked")
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

    public static <T extends Throwable> @Nullable T findNestedCause(@NotNull Throwable t, @NotNull Class<T> cls) {
        return (cls.isInstance(t) ? cls.cast(t) : ((t.getCause() == null) ? null : findNestedCause(t.getCause(), cls)));
    }

    public static @NotNull String getPart(@NotNull String str, @NotNull @NonNls @Language("RegExp") String separator, @NotNull Parts part) {
        Matcher m = Regex.getMatcher(separator, str);

        switch(part) {
            case NOT_FIRST:
                if(m.find()) return str.substring(m.end());
                return str;
            case NOT_LAST:
                if(m.find()) {
                    int i = m.start();
                    while(m.find()) i = m.start();
                    return str.substring(0, i);
                }
                return str;
            case LAST:
                if(m.find()) {
                    int i = m.end();
                    while(m.find()) i = m.end();
                    return str.substring(i);
                }
                return str;
            default:
                if(m.find()) return str.substring(0, m.start());
                return str;
        }
    }

    public static int @NotNull [] getRange(int start, int end, int stride) {
        if(stride == 0) throw new IllegalArgumentException(msgs.getString("msg.err.get_range.stride_zero"));
        if(start == end) return new int[0];
        if(start < end && stride < 0) throw new IllegalArgumentException(msgs.getString("msg.err.get_range.stride_lt_zero"));
        if(start > end && stride > 0) throw new IllegalArgumentException(msgs.getString("msg.err.get_range.stide_gt_zero"));

        boolean _flg = (start < end);
        long    _ds  = (Math.max(start, (long)end) - Math.min(start, (long)end));
        long    _st  = Math.abs(stride);
        long    _sz  = ((_ds / _st) + (((_ds % _st) == 0) ? 0 : 1));
        int     _idx = 0;

        if(_sz > Integer.MAX_VALUE) throw new IllegalArgumentException(msgs.format("msg.err.get_range.too_many_elements", _sz, Integer.MAX_VALUE));

        int[] arr = new int[(int)_sz];

        for(long i = start; (_flg ? (i < (long)end) : (i > (long)end)); i += stride) arr[_idx++] = (int)i;
        if(_idx < arr.length) throw new IllegalArgumentException(msgs.format("msg.err.get_range.guess_too_high", _idx, arr.length));
        return arr;
    }

    public static @NotNull <T extends Throwable> T getThrowable(@NotNull String msg, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class).newInstance(msg); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull String ifNullOrEmpty(@Nullable String str, @NotNull String def) {
        return ((str == null || str.length() <= 0) ? def : str);
    }

    @Contract(pure = true)
    public static boolean isByteIn(byte num, byte @NotNull ... others) {
        for(byte other : others) if(num == other) return true;
        return false;
    }

    @Contract(pure = true)
    public static boolean isCharIn(char ch, char @NotNull ... others) {
        for(char other : others) if(ch == other) return true;
        return false;
    }

    @Contract(pure = true)
    public static boolean isDoubleIn(double dbl, double @NotNull ... others) {
        for(double other : others) if(dbl == other) return true;
        return false;
    }

    @Contract(pure = true)
    @SafeVarargs
    @Deprecated(forRemoval = true)
    public static <T> boolean isEqualToAny(T obj, T @NotNull ... others) { return isObjIn(obj, others); }

    @Contract(pure = true)
    @Deprecated(forRemoval = true)
    public static boolean isEqualToAnyByte(byte num, byte @NotNull ... others) { return isByteIn(num, others); }

    @Contract(pure = true)
    @Deprecated(forRemoval = true)
    public static boolean isEqualToAnyChar(char ch, char @NotNull ... others) { return isCharIn(ch, others); }

    @Contract(pure = true)
    @Deprecated(forRemoval = true)
    public static boolean isEqualToAnyDouble(double dbl, double @NotNull ... others) { return isDoubleIn(dbl, others); }

    @Contract(pure = true)
    @Deprecated(forRemoval = true)
    public static boolean isEqualToAnyFloat(float flt, float @NotNull ... others) { return isFloatIn(flt, others); }

    @Contract(pure = true)
    @Deprecated(forRemoval = true)
    public static boolean isEqualToAnyInt(int num, int @NotNull ... others) { return isIntIn(num, others); }

    @Contract(pure = true)
    @Deprecated(forRemoval = true)
    public static boolean isEqualToAnyLong(long num, long @NotNull ... others) { return isLongIn(num, others); }

    @Contract(pure = true)
    @Deprecated(forRemoval = true)
    public static boolean isEqualToAnyShort(short num, short @NotNull ... others) { return isShortIn(num, others); }

    @Contract(pure = true)
    public static boolean isFloatIn(float flt, float @NotNull ... others) {
        for(float other : others) if(flt == other) return true;
        return false;
    }

    @Contract(pure = true)
    public static boolean isIntIn(int num, int @NotNull ... others) {
        for(int other : others) if(num == other) return true;
        return false;
    }

    @Contract(pure = true)
    public static boolean isLongIn(long num, long @NotNull ... others) {
        for(long other : others) if(num == other) return true;
        return false;
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> boolean isObjIn(T obj, T @NotNull ... others) {
        for(T other : others) if(Objects.equals(obj, other)) return true;
        return false;
    }

    @Contract(pure = true)
    public static boolean isShortIn(short num, short @NotNull ... others) {
        for(short other : others) if(num == other) return true;
        return false;
    }

    public static @NotNull String join(char separator, Object... args) {
        return join(Character.toString(separator), args);
    }

    public static @NotNull String join(@NotNull String separator, Object @NotNull ... args) {
        if(args.length == 0) return "";
        StringBuilder sb = new StringBuilder().append(args[0]);
        for(int i = 1; i < args.length; i++) sb.append(separator).append(args[i]);
        return sb.toString();
    }

    public static <T> T @NotNull [] join(T @NotNull [] array1, T @NotNull [] array2) {
        T[] copy = newArray(array1.getClass(), (array1.length + array2.length));
        System.arraycopy(array1, 0, copy, 0, array1.length);
        System.arraycopy(array2, 0, copy, array1.length, array2.length);
        return copy;
    }

    @Contract("!null -> !null; null -> null")
    public static String lc(@Nullable String str) {
        return ((str == null) ? null : str.toLowerCase());
    }

    public static boolean nz(@Nullable String str) {
        return ((str != null) && (str.trim().length() > 0));
    }

    public static <T> T @NotNull [] prepend(T obj, T @NotNull [] array) {
        T[] copy = newArray(array.getClass(), (array.length + 1));
        System.arraycopy(array, 0, copy, 1, array.length);
        copy[0] = obj;
        return copy;
    }

    public static @NotNull String sha3_256Hash(@NotNull String text) {
        return sha3_256Hash(text.toCharArray());
    }

    public static @NotNull String sha3_256Hash(char @NotNull [] chars) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            ByteBuffer    bytes  = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(chars));
            digest.update(bytes);
            String str = Base64.getEncoder().encodeToString(digest.digest());
            for(int i = 0; i < bytes.limit(); i++) bytes.put(i, (byte)0);
            return str;
        }
        catch(NoSuchAlgorithmException | CharacterCodingException e) { throw new RuntimeException(e); }
    }

    public static @NotNull String @NotNull [] splitDotPath(@NotNull String path) {
        int i = path.lastIndexOf('.');
        return ((i >= 0) ? new String[] { path.substring(0, i), path.substring(i + 1) } : new String[] { path });
    }

    public static char[] tr(char[] chars) {
        if((chars == null) || (chars.length == 0)) return chars;
        int[] range = tr1(chars);
        if(range[1] == 0) return new char[0];
        if((range[0] == 0) && (range[1] == chars.length)) return chars;
        return Arrays.copyOfRange(chars, range[0], range[1]);
    }

    @Contract("!null -> !null; null -> null")
    public static String tr(@Nullable String str) {
        return ((str == null) ? null : str.trim());
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static @NotNull <P, R> R @NotNull [] translate(@NotNull Class<R> cls, @NotNull GetWithValueDelegate<P, R> delegate, @NotNull P @NotNull ... args) {
        R[] out = (R[])Array.newInstance(cls, args.length);
        for(int i = 0; i < args.length; i++) out[i] = delegate.action(args[i]);
        return out;
    }

    @Contract("!null -> !null; null -> null")
    public static String uc(@Nullable String str) {
        return ((str == null) ? null : str.toUpperCase());
    }

    /**
     * Quick way to wrap elements in an array.
     *
     * @param elements The elements to return as an array.
     * @return An array of the elements.
     */
    @SafeVarargs
    public static <T> T[] wrap(T... elements) { return elements; }

    /**
     * Quick way to wrap characters in an array.
     *
     * @param characters The characters to return as an array.
     * @return An array of the characters.
     */
    public static char[] wrap(char... characters) { return characters; }

    /**
     * Quick way to wrap integer numbers in an array.
     *
     * @param numbers The integer numbers to return as an array.
     * @return An array of the integer numbers.
     */
    public static int[] wrap(int... numbers) { return numbers; }

    /**
     * Quick way to wrap bytes in an array.
     *
     * @param numbers The bytes to return as an array.
     * @return An array of the bytes.
     */
    public static byte[] wrap(byte... numbers) { return numbers; }

    /**
     * Quick way to wrap short integer numbers in an array.
     *
     * @param numbers The short integer numbers to return as an array.
     * @return An array of the short integer numbers.
     */
    public static short[] wrap(short... numbers) { return numbers; }

    /**
     * Quick way to wrap long integer numbers in an array.
     *
     * @param numbers The long integer numbers to return as an array.
     * @return An array of the long integer numbers.
     */
    public static long[] wrap(long... numbers) { return numbers; }

    /**
     * Quick way to wrap single precision floating point numbers in an array.
     *
     * @param numbers The single precision floating point numbers to return as an array.
     * @return An array of the single precision floating point numbers.
     */
    public static float[] wrap(float... numbers) { return numbers; }

    /**
     * Quick way to wrap double precision floating point numbers in an array.
     *
     * @param numbers The double precision floating point numbers to return as an array.
     * @return An array of the double precision floating point numbers.
     */
    public static double[] wrap(double... numbers) { return numbers; }

    /**
     * Quick way to wrap boolean values in an array.
     *
     * @param bools The boolean values to return as an array.
     * @return An array of the boolean values.
     */
    public static boolean[] wrap(boolean... bools) { return bools; }

    public static @NotNull <T extends Throwable> T wrapThrowable(@Nullable String msg, @NotNull Throwable t, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class, Throwable.class).newInstance(((msg == null) ? t.getMessage() : msg), t); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@NotNull Throwable t, @NotNull Class<T> throwableClass) {
        return wrapThrowable(null, t, throwableClass);
    }

    public static boolean z(char[] chars) {
        return ((chars == null) || (chars.length == 0) || (tr1(chars)[1] == 0));
    }

    public static boolean z(@Nullable String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(Class<? extends Object[]> arrayType, int length) {
        return ((arrayType == Object[].class) ? (T[])new Object[length] : (T[])Array.newInstance(arrayType.getComponentType(), length));
    }

    private static int @NotNull [] tr1(char @NotNull [] chars) {
        for(int i = 0; i < chars.length; ) {
            int[] cp = codePointAt(chars, i);
            if(!Character.isWhitespace(cp[0])) return tr2(chars, i);
            i = cp[1];
        }
        return new int[] { 0, 0 };
    }

    private static int @NotNull [] tr2(char @NotNull [] chars, int i) {
        for(int j = chars.length; j > i; ) {
            int[] cp = codePointAt(chars, j, true);
            if(!Character.isWhitespace(cp[0])) return new int[] { i, j };
            j = cp[1];
        }
        return new int[] { i, (i + 1) };
    }

    public enum Parts {
        FIRST, LAST, NOT_FIRST, NOT_LAST
    }
}
