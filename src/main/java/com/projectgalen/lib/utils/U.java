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

import com.projectgalen.lib.utils.delegates.ThrowingSupplier;
import com.projectgalen.lib.utils.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;

@SuppressWarnings({ "unused", "SameParameterValue", "unchecked" })
public final class U {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private U() { }

    public static <T extends Comparable<T>> int compare(boolean sortNullFirst, @Nullable T lhs, @Nullable T rhs) {
        return (((lhs == null) && (rhs == null)) ? 0 : ((lhs == null) ? (sortNullFirst ? -1 : 1) : ((rhs == null) ? (sortNullFirst ? 1 : -1) : lhs.compareTo(rhs))));
    }

    public static @NotNull StringBuilder appendFormat(@NotNull StringBuilder sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
    }

    public static @NotNull StringBuffer appendFormat(@NotNull StringBuffer sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
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
        return ((str.isEmpty()) ? str : ((str.length() == 1) ? str.toUpperCase() : (str.substring(0, 1).toUpperCase() + str.substring(1))));
    }

    public static @NotNull String cleanNumberString(String numberString) {
        return toNonEmptyString(Objects.toString(numberString, "0").replaceAll("[^0-9.+-]", ""), "0");
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

    public static <T> void doIfChanged(T object1, T object2, @NotNull BiConsumer<T, T> biConsumer) {
        if(!Objects.equals(object1, object2)) biConsumer.accept(object1, object2);
    }

    public static @NotNull String getPart(@NotNull String str, @NotNull @NonNls @Language("RegExp") String separator, @NotNull Parts part) {
        Matcher m = Regex.getMatcher(separator, str);
        return switch(part) {/*@f0*/
            case NOT_FIRST -> (m.find() ? str.substring(m.end()) : str);
            case NOT_LAST  -> (m.find() ? str.substring(0, getLastMatchLocation(m, Matcher::start)) : str);
            case LAST      -> (m.find() ? str.substring(getLastMatchLocation(m, Matcher::end)) : str);
            default        -> (m.find() ? str.substring(0, m.start()) : str);
        };/*@f1*/
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

    public static <R> R getSafe(boolean debug, @NotNull ThrowingSupplier<R> supplier) { return getSafe(debug, null, supplier); }

    public static <R> R getSafe(boolean debug, R defaultValue, @NotNull ThrowingSupplier<R> supplier) {
        try {
            return supplier.get();
        }
        catch(Throwable t) {
            if(debug) t.printStackTrace(System.err);
            return defaultValue;
        }
    }

    public static <R> R getSafe(R defaultValue, @NotNull ThrowingSupplier<R> supplier) { return getSafe(false, defaultValue, supplier); }

    public static <R> R getSafe(@NotNull ThrowingSupplier<R> supplier)                 { return getSafe(false, null, supplier); }

    public static @NotNull String ifNullOrEmpty(@Nullable String str, @NotNull String def) {
        return ((str == null || str.isEmpty()) ? def : str);
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
    public static boolean isCharIn(char ch, @NotNull String others) {
        return isCharIn(ch, others.toCharArray());
    }

    @Contract(pure = true)
    public static boolean isDoubleIn(double dbl, double @NotNull ... others) {
        for(double other : others) if(dbl == other) return true;
        return false;
    }

    public static boolean isFlagCleared(long value, int flag) {
        return ((value & flag) != flag);
    }

    public static boolean isFlagSet(long value, int flag) {
        return ((value & flag) == flag);
    }

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

    public static @NotNull String join(char separator, Object @NotNull ... args) {
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

    @SafeVarargs
    public static @NotNull <P, R> R @NotNull [] map(@NotNull Class<R> cls, @NotNull Function<P, R> delegate, @NotNull P @NotNull ... args) {
        R[] out = (R[])Array.newInstance(cls, args.length);
        for(int i = 0; i < args.length; i++) out[i] = delegate.apply(args[i]);
        return out;
    }

    public static <T extends Comparable<T>> int myCompare(T obj1, T obj2) {
        return ((obj1 == obj2) ? 0 : ((obj1 == null) ? -1 : ((obj2 == null) ? 1 : obj1.compareTo(obj2))));
    }

    @Contract(pure = true)
    public static @Nullable String nullIfEmpty(@Nullable String str) {
        return (U.z(str) ? null : str);
    }

    public static boolean nz(@Nullable String str) {
        return ((str != null) && (!str.trim().isEmpty()));
    }

    public static @NotNull String @NotNull [] splitDotPath(@NotNull String path) {
        int i = path.lastIndexOf('.');
        return ((i >= 0) ? new String[] { path.substring(0, i), path.substring(i + 1) } : new String[] { path });
    }

    public static @NotNull String toNonEmptyString(@Nullable String str, @NotNull String defaultString) {
        return toNonEmptyString(str, () -> defaultString);
    }

    public static @NotNull String toNonEmptyString(@Nullable String str, @NotNull Supplier<String> defaultSupplier) {
        String s = ((str == null) ? "" : str.trim());
        return (s.isEmpty() ? defaultSupplier.get() : s);
    }

    public static String toString(@Nullable String str, @NotNull Supplier<String> supplier) {
        return Optional.ofNullable(str).orElseGet(supplier);
    }

    @Contract("!null -> !null; null -> null")
    public static String tr(@Nullable String str) {
        return ((str == null) ? null : str.trim());
    }

    @Contract("!null -> !null; null -> null")
    public static String uc(@Nullable String str) {
        return ((str == null) ? null : str.toUpperCase());
    }

    public static boolean z(@Nullable String str) {
        return ((str == null) || (str.trim().isEmpty()));
    }

    private static int getLastMatchLocation(Matcher m, @NotNull Function<Matcher, Integer> function) {
        int i = function.apply(m);
        while(m.find()) i = function.apply(m);
        return i;
    }

    public enum Parts {
        FIRST, LAST, NOT_FIRST, NOT_LAST
    }
}
