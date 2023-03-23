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
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

@SuppressWarnings({ "unused", "SameParameterValue" })
public final class U {

    private U() { }

    public static @NotNull StringBuilder appendFormat(@NotNull StringBuilder sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
    }

    public static @NotNull StringBuffer appendFormat(@NotNull StringBuffer sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
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

    public static @NotNull StringBuffer concat(@NotNull StringBuffer sb, Object... args) {
        for(Object o : args) sb.append(o);
        return sb;
    }

    public static @NotNull StringBuilder concat(@NotNull StringBuilder sb, Object... args) {
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

    public static @NotNull <T extends Throwable> T getThrowable(@NotNull String msg, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class).newInstance(msg); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull String ifNullOrEmpty(@Nullable String str, @NotNull String def) {
        return ((str == null || str.length() <= 0) ? def : str);
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

    @Contract("!null -> !null; null -> null")
    public static String lc(@Nullable String str) {
        return ((str == null) ? null : str.toLowerCase());
    }

    public static boolean nz(@Nullable String str) {
        return ((str != null) && (str.trim().length() > 0));
    }

    public static @NotNull String @NotNull [] splitDotPath(@NotNull String path) {
        int i = path.lastIndexOf('.');
        return ((i >= 0) ? new String[] { path.substring(0, i), path.substring(i + 1) } : new String[] { path });
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

    public static @NotNull <T extends Throwable> T wrapThrowable(@Nullable String msg, @NotNull Throwable t, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class, Throwable.class).newInstance(((msg == null) ? t.getMessage() : msg), t); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@NotNull Throwable t, @NotNull Class<T> throwableClass) {
        return wrapThrowable(null, t, throwableClass);
    }

    public static boolean z(@Nullable String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    public enum Parts {
        FIRST, LAST, NOT_FIRST, NOT_LAST
    }
}
