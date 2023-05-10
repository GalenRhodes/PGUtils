package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGCollections.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 10, 2023
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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Collection;

public final class PGCollections {
    public PGCollections() { }

    public static <T, C extends Collection<T>> BigDecimal bigDecimalSum(@NotNull C collection, @NotNull GetBigDecFromDelegate<T> delegate) {
        return bigDecimalSum(collection, MathContext.UNLIMITED, delegate);
    }

    public static <T, C extends Collection<T>> BigDecimal bigDecimalSum(@NotNull C collection, MathContext mathContext, @NotNull GetBigDecFromDelegate<T> delegate) {
        BigDecimal sum = null;
        for(T obj : collection) sum = ((sum == null) ? delegate.getBigDecimal(obj) : sum.add(delegate.getBigDecimal(obj), mathContext));
        return ((sum == null) ? BigDecimal.ZERO : sum);
    }

    public static <T, C extends Collection<T>> BigInteger bigIntegerSum(@NotNull C collection, @NotNull GetBigIntFromDelegate<T> delegate) {
        BigInteger sum = BigInteger.ZERO;
        for(T obj : collection) sum = sum.add(delegate.getBigInteger(obj));
        return sum;
    }

    public static <T, C extends Collection<T>> double doubleSum(@NotNull C collection, @NotNull GetDoubleFromDelegate<T> delegate) {
        double sum = 0;
        for(T obj : collection) sum += delegate.getDouble(obj);
        return sum;
    }

    public static <T, C extends Collection<T>> float floatSum(@NotNull C collection, @NotNull GetFloatFromDelegate<T> delegate) {
        float sum = 0;
        for(T obj : collection) sum += delegate.getFloat(obj);
        return sum;
    }

    public static <T, C extends Collection<T>> int intSum(@NotNull C collection, @NotNull GetIntFromDelegate<T> delegate) {
        int sum = 0;
        for(T obj : collection) sum += delegate.getInt(obj);
        return sum;
    }

    public static <T, C extends Collection<T>> long longSum(@NotNull C collection, @NotNull GetLongFromDelegate<T> delegate) {
        long sum = 0;
        for(T obj : collection) sum += delegate.getLong(obj);
        return sum;
    }

    public interface GetBigDecFromDelegate<T> {
        BigDecimal getBigDecimal(@NotNull T obj);
    }

    public interface GetBigIntFromDelegate<T> {
        BigInteger getBigInteger(@NotNull T obj);
    }

    public interface GetDoubleFromDelegate<T> {
        double getDouble(@NotNull T obj);
    }

    public interface GetFloatFromDelegate<T> {
        float getFloat(@NotNull T obj);
    }

    public interface GetIntFromDelegate<T> {
        int getInt(@NotNull T obj);
    }

    public interface GetLongFromDelegate<T> {
        long getLong(@NotNull T obj);
    }
}
