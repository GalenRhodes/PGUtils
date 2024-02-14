package com.projectgalen.lib.utils.streams;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Streams.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 24, 2023
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

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.collections.items.CollectionItem;
import com.projectgalen.lib.utils.collections.items.IntCollectionItem;
import com.projectgalen.lib.utils.collections.items.iterators.ArrayIterator;
import com.projectgalen.lib.utils.collections.items.iterators.IntArrayIterator;
import com.projectgalen.lib.utils.math.Range;
import com.projectgalen.lib.utils.refs.BooleanRef;
import com.projectgalen.lib.utils.refs.IntegerRef;
import com.projectgalen.lib.utils.text.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.*;
import static java.util.Spliterators.spliterator;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;
import static java.util.stream.StreamSupport.stream;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public class Streams {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private static final String ERR_MSG_01      = msgs.getString("msg.err.index_out_of_range");
    private static final String TEXT_START      = msgs.getString("text.start");
    private static final String TEXT_END        = msgs.getString("text.end");
    private static final String FMT_LT          = msgs.getString("fmt.lt");
    private static final String FMT_GT          = msgs.getString("fmt.gt");
    private static final int    CHARACTERISTICS = (IMMUTABLE | ORDERED | DISTINCT);

    /**
     * A summing collector for {@link BigDecimal} values that maintains precision.
     */
    public static final Collector<BigDecimal, AtomicReference<BigDecimal>, BigDecimal> sumBigDecimal = new Collector<>() {
        @Contract(pure = true) public @Override @NotNull BiConsumer<AtomicReference<BigDecimal>, BigDecimal> accumulator() { return (a, v) -> a.accumulateAndGet(v, BigDecimal::add); }

        @Contract(value = " -> new", pure = true) public @Override @NotNull @Unmodifiable Set<Characteristics> characteristics() { return Set.of(CONCURRENT, UNORDERED); }

        @Contract(pure = true) public @Override @NotNull BinaryOperator<AtomicReference<BigDecimal>> combiner() { return (a, b) -> new AtomicReference<BigDecimal>(a.get().add(b.get())); }

        @Contract(pure = true) public @Override @NotNull Function<AtomicReference<BigDecimal>, BigDecimal> finisher() { return AtomicReference::get; }

        @Contract(value = " -> new", pure = true) public @Override @NotNull Supplier<AtomicReference<BigDecimal>> supplier() { return () -> new AtomicReference<BigDecimal>(BigDecimal.ZERO); }
    };
    /**
     * A summing collector for {@link BigInteger} values that maintains precision.
     */
    public static final Collector<BigInteger, AtomicReference<BigInteger>, BigInteger> sumBigInteger = new Collector<>() {
        public @Contract(pure = true) @Override @NotNull BiConsumer<AtomicReference<BigInteger>, BigInteger> accumulator() { return (a, v) -> a.accumulateAndGet(v, BigInteger::add); }

        public @Contract(value = " -> new", pure = true) @Override @NotNull @Unmodifiable Set<Characteristics> characteristics() { return Set.of(CONCURRENT, UNORDERED); }

        public @Contract(pure = true) @Override @NotNull BinaryOperator<AtomicReference<BigInteger>> combiner() { return (a, b) -> new AtomicReference<BigInteger>(a.get().add(b.get())); }

        public @Contract(pure = true) @Override @NotNull Function<AtomicReference<BigInteger>, BigInteger> finisher() { return AtomicReference::get; }

        public @Contract(value = " -> new", pure = true) @Override @NotNull Supplier<AtomicReference<BigInteger>> supplier() { return () -> new AtomicReference<BigInteger>(BigInteger.ZERO); }
    };

    public Streams() { }

    /**
     * Create a stream of {@link CollectionItem} from the elements of an array.
     *
     * @param array The elements of the array.
     *
     * @return A stream of {@link CollectionItem}.
     */
    public static @SafeVarargs <T> @NotNull Stream<CollectionItem<T>> arrayStream(T @NotNull ... array) {
        return arrayStream(array, 0, array.length);
    }

    public static <T> @NotNull Stream<CollectionItem<T>> arrayStream(T @NotNull [] array, int startIndex, int endIndex) {
        validateIndexes(array.length, startIndex, endIndex);
        return stream(spliterator(new ArrayIterator<T>(array, startIndex, endIndex), (endIndex - startIndex), CHARACTERISTICS), false);
    }

    /**
     * Create a stream of {@link IntCollectionItem} from the elements of an array.
     *
     * @param array The elements of the array.
     *
     * @return A stream of {@link IntCollectionItem}.
     */
    public static @NotNull Stream<IntCollectionItem> arrayStream(int... array) {
        return arrayStream(array, 0, array.length);
    }

    public static @NotNull Stream<IntCollectionItem> arrayStream(int @NotNull [] array, int startIndex, int endIndex) {
        validateIndexes(array.length, startIndex, endIndex);
        return stream(spliterator(new IntArrayIterator(array, startIndex, endIndex), (endIndex - startIndex), CHARACTERISTICS), false);
    }

    public static @Contract(pure = true) @NotNull IntStream closedIntRange(int startInclusive, int endInclusive) {
        return closedIntRange(startInclusive, endInclusive, ((startInclusive <= endInclusive) ? 1 : -1));
    }

    public static @Contract(pure = true) @NotNull IntStream closedIntRange(int startInclusive, int endInclusive, int step) {
        if((startInclusive <= endInclusive) && (step == 1)) return IntStream.rangeClosed(startInclusive, endInclusive);
        return StreamSupport.intStream(new ClosedRangeIterator(startInclusive, endInclusive, step), false);
    }

    public static IntStream intRange(int startInclusive, int endExclusive) {
        return intRange(startInclusive, endExclusive, ((startInclusive <= endExclusive) ? 1 : -1));
    }

    public static IntStream intRange(int startInclusive, int endExclusive, int step) {
        if(startInclusive == endExclusive) return IntStream.empty();
        if((startInclusive < endExclusive) && (step == 1)) return IntStream.range(startInclusive, endExclusive);
        return StreamSupport.intStream(new RangeIterator(startInclusive, endExclusive, step), false);
    }

    public static <T> @NotNull Stream<CollectionItem<T>> listStream(@NotNull List<T> list) {
        return streamWith(new IndexedListIterator<>(list));
    }

    public static @NotNull Stream<String> splitStream(@Nullable String val) {
        return splitStream(val, PGProperties.DEFAULT_LIST_SEPARATOR_RX);
    }

    public static @NotNull Stream<String> splitStream(@Nullable String val, @RegExp @Language("RegExp") @NotNull @NonNls String regexp) {
        if(val == null) return Stream.empty();
        Matcher    m = Regex.getMatcher(regexp, val);
        IntegerRef p = new IntegerRef(0);
        return streamWith(done -> {
            String string = null;
            if(p.value == -1) {
                done.value = true;
            }
            else if(m.find()) {
                string  = val.substring(p.value, m.start());
                p.value = m.end();
            }
            else {
                string  = val.substring(p.value);
                p.value = -1;
            }
            return string;
        });
    }

    public static <T> @NotNull Stream<T> streamOf(@NotNull Class<T> cls, Object @NotNull [] objs) {
        return streamOf(cls, Stream.of(objs));
    }

    public static <T> @NotNull Stream<T> streamOf(@NotNull Class<T> cls, @NotNull Collection<?> c) {
        return streamOf(cls, c.stream());
    }

    public static <T> @NotNull Stream<T> streamOf(@NotNull Class<T> cls, @NotNull Stream<?> stream) {
        return stream.filter(cls::isInstance).map(cls::cast);
    }

    public static @NotNull <T> Stream<T> streamWith(@NotNull Streams.StreamItemProvider<T> provider) {
        return stream(Spliterators.spliteratorUnknownSize(new LambdaIterator<>(provider), IMMUTABLE), false);
    }

    private static <T> void validateIndexes(int arrayLength, int startIndex, int endIndex) {
        if(startIndex < 0) throw new IllegalArgumentException(ERR_MSG_01.formatted(TEXT_START, FMT_LT.formatted(startIndex, 0)));
        if(endIndex > arrayLength) throw new IllegalArgumentException(ERR_MSG_01.formatted(TEXT_END, FMT_GT.formatted(endIndex, arrayLength)));
        if(startIndex > endIndex) throw new IllegalArgumentException(ERR_MSG_01.formatted(TEXT_START, FMT_GT.formatted(startIndex, endIndex)));
    }

    private enum LambdaIteratorState {Yes, No, Unkown}

    public interface StreamItemProvider<T> {
        T provide(@NotNull BooleanRef done);
    }

    private static class ClosedRangeIterator extends Spliterators.AbstractIntSpliterator {

        protected final int     start;
        protected final int     end;
        protected final int     step;
        protected final boolean up;
        protected       int     next;

        public ClosedRangeIterator(int start, int end, int step) {
            super(com.projectgalen.lib.utils.math.Range.closedRangeCount(start, end, step), SIZED | NONNULL | ORDERED | IMMUTABLE | DISTINCT);
            this.start = start;
            this.end   = end;
            this.step  = step;
            this.next  = start;
            this.up    = (start <= end);
        }

        public @Override boolean tryAdvance(IntConsumer action) {
            if(!hasNext()) return false;
            action.accept(next);
            next += step;
            return true;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted") protected boolean hasNext() {
            return (up ? (next <= end) : (next >= end));
        }
    }

    private static final class IndexedListIterator<T> implements StreamItemProvider<CollectionItem<T>> {
        private final List<T> list;
        private       int     index = 0;

        public IndexedListIterator(List<T> list) {
            this.list = list;
        }

        public @Override @Nullable CollectionItem<T> provide(@NotNull BooleanRef done) {
            if(index < list.size()) {
                CollectionItem<T> i = new CollectionItem<>(index, list.get(index));
                index++;
                return i;
            }
            done.value = true;
            return null;
        }
    }

    private static final class LambdaIterator<T> implements Iterator<T> {
        private final StreamItemProvider<T> provider;
        private       LambdaIteratorState   state = LambdaIteratorState.Unkown;
        private       T                     last  = null;

        public LambdaIterator(StreamItemProvider<T> provider) {
            this.provider = provider;
        }

        public @Override boolean hasNext() {
            if(state == LambdaIteratorState.Unkown) {
                BooleanRef done = new BooleanRef(false);
                last  = provider.provide(done);
                state = (done.value ? LambdaIteratorState.No : LambdaIteratorState.Yes);
            }
            return (state == LambdaIteratorState.Yes);
        }

        public @Override T next() {
            if(hasNext()) {
                T obj = last;
                last  = null;
                state = LambdaIteratorState.Unkown;
                return obj;
            }
            throw new NoSuchElementException();
        }
    }

    private static class RangeIterator extends Spliterators.AbstractIntSpliterator {

        protected final int     start;
        protected final int     end;
        protected final int     step;
        protected final boolean up;
        protected       int     next;

        public RangeIterator(int start, int end, int step) {
            super(Range.rangeCount(start, end, step), SIZED | NONNULL | ORDERED | IMMUTABLE | DISTINCT);
            this.start = start;
            this.end   = end;
            this.step  = step;
            this.next  = start;
            this.up    = (start <= end);
        }

        public @Override boolean tryAdvance(IntConsumer action) {
            if(!hasNext()) return false;
            action.accept(next);
            next += step;
            return true;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted") protected boolean hasNext() {
            return (up ? (next < end) : (next > end));
        }
    }
}
