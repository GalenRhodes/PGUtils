package com.projectgalen.lib.utils.collections;

// ===============================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: PGCollections.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 10, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===============================================================================================================================

import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.collections.items.CollectionItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

@SuppressWarnings("unused")
public final class PGCollections {

    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    public PGCollections() { }

    public static <K, V> @NotNull Map<K, V> asMap(K @NotNull [] keys, V @NotNull [] vals) {
        if(keys.length != vals.length) throw new IllegalArgumentException(msgs.format("msg.err.key_count_value_count_mismatch", keys.length, vals.length));
        Map<K, V> map = new LinkedHashMap<>();
        IntStream.range(0, keys.length).forEach(i -> map.put(keys[i], vals[i]));
        return map;
    }

    public static <T, C extends Collection<T>> BigDecimal bigDecimalSum(@NotNull C collection, @NotNull Function<T, BigDecimal> delegate) {
        return bigDecimalSum(collection, MathContext.UNLIMITED, delegate);
    }

    public static <T, C extends Collection<T>> BigDecimal bigDecimalSum(@NotNull C collection, MathContext mathContext, @NotNull Function<T, BigDecimal> delegate) {
        BigDecimal sum = null;
        for(T obj : collection)
            sum = ((sum == null) ? delegate.apply(obj) : sum.add(delegate.apply(obj), mathContext));
        return ((sum == null) ? BigDecimal.ZERO : sum);
    }

    public static <T, C extends Collection<T>> BigInteger bigIntegerSum(@NotNull C collection, @NotNull Function<T, BigInteger> delegate) {
        BigInteger sum = BigInteger.ZERO;
        for(T obj : collection) sum = sum.add(delegate.apply(obj));
        return sum;
    }

    public static <T> @NotNull List<T> copyAndClear(@NotNull List<T> list) {
        return copyAndClear(list, ArrayList::new);
    }

    public static <T> @NotNull List<T> copyAndClear(@NotNull List<T> list, @NotNull Supplier<List<T>> listSupplier) {
        List<T> clone = listSupplier.get();
        clone.addAll(list);
        list.clear();
        return clone;
    }

    public static <T, C extends Collection<T>> double doubleSum(@NotNull C collection, @NotNull Function<T, Double> delegate) {
        double sum = 0;
        for(T obj : collection) sum += delegate.apply(obj);
        return sum;
    }

    public static <T, C extends Collection<T>> float floatSum(@NotNull C collection, @NotNull Function<T, Float> delegate) {
        float sum = 0;
        for(T obj : collection) sum += delegate.apply(obj);
        return sum;
    }

    public static <T> T getFirst(@Nullable List<T> list) {
        return (((list == null) || list.isEmpty()) ? null : list.getFirst());
    }

    public static <T> T getLast(@Nullable List<T> list) {
        return (((list == null) || list.isEmpty()) ? null : list.getLast());
    }

    public static <T> Stream<CollectionItem<T>> indexedStream(@NotNull Collection<T> c) {
        List<T>                    list    = ((c instanceof List) ? ((List<T>)c) : new ArrayList<T>(c));
        Builder<CollectionItem<T>> builder = Stream.builder();
        for(int i = 0, j = list.size(); i < j; i++) builder.accept(new CollectionItem<>(i, list.get(i)));
        return builder.build();
    }

    public static <T, C extends Collection<T>> int intSum(@NotNull C collection, @NotNull Function<T, Integer> delegate) {
        int sum = 0;
        for(T obj : collection) sum += delegate.apply(obj);
        return sum;
    }

    public static <T, C extends Collection<T>> long longSum(@NotNull C collection, @NotNull Function<T, Long> delegate) {
        long sum = 0;
        for(T obj : collection) sum += delegate.apply(obj);
        return sum;
    }

    public static <T extends Comparable<T>, L extends List<T>> @Contract("null -> null; !null -> param1") L sort(L list) {
        if(list != null) list.sort(Comparable::compareTo);
        return list;
    }

    public static <T, L extends List<T>> @Contract("null,_ -> null; !null,_ -> param1") L sort(L list, @NotNull Comparator<T> comparator) {
        if(list != null) list.sort(comparator);
        return list;
    }
}
