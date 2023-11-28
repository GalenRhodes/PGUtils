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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({ "unused", "SameParameterValue", "unchecked" })
public final class U {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private U() { }

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

    public static <T extends Comparable<T>> int compare(@Nullable T lhs, @Nullable T rhs) {
        return compare(true, lhs, rhs);
    }

    public static <T extends Comparable<T>> int compare(boolean sortNullFirst, @Nullable T lhs, @Nullable T rhs) {
        return (((lhs == null) && (rhs == null)) ? 0 : ((lhs == null) ? (sortNullFirst ? -1 : 1) : ((rhs == null) ? (sortNullFirst ? 1 : -1) : lhs.compareTo(rhs))));
    }

    public static <T> void doIfChanged(T object1, T object2, @NotNull BiConsumer<T, T> biConsumer) {
        if(!Objects.equals(object1, object2)) biConsumer.accept(object1, object2);
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
    public static @SafeVarargs <T> boolean isObjIn(T obj, T @NotNull ... others) {
        for(T other : others) if(Objects.equals(obj, other)) return true;
        return false;
    }

    @Contract(pure = true)
    public static boolean isShortIn(short num, short @NotNull ... others) {
        for(short other : others) if(num == other) return true;
        return false;
    }

    public static @SafeVarargs @NotNull <P, R> R @NotNull [] map(@NotNull Class<R> cls, @NotNull Function<P, R> delegate, @NotNull P @NotNull ... args) {
        R[] out = (R[])Array.newInstance(cls, args.length);
        for(int i = 0; i < args.length; i++) out[i] = delegate.apply(args[i]);
        return out;
    }

    public static <T extends Comparable<T>> int myCompare(T obj1, T obj2) {
        return ((obj1 == obj2) ? 0 : ((obj1 == null) ? -1 : ((obj2 == null) ? 1 : obj1.compareTo(obj2))));
    }
}
