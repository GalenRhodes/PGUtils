package com.projectgalen.lib.utils;

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

import com.projectgalen.lib.utils.collections.CollectionItem;
import com.projectgalen.lib.utils.refs.BooleanRef;
import com.projectgalen.lib.utils.refs.IntegerRef;
import com.projectgalen.lib.utils.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public class Streams {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    public Streams() { }

    /**
     * Create a stream of {@link CollectionItem} from the elements of an array.
     *
     * @param array The elements of the array.
     *
     * @return A stream of {@link CollectionItem}.
     */
    public static @SafeVarargs <T> @NotNull Stream<CollectionItem<T>> arrayStream(T @NotNull ... array) {
        ArrayIterator<T>               iterator    = new ArrayIterator<>(array);
        Spliterator<CollectionItem<T>> spliterator = Spliterators.spliterator(iterator, array.length, Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.DISTINCT);
        return StreamSupport.stream(spliterator, false);
    }

    @Contract(pure = true) public static @NotNull IntStream closedIntRange(int startInclusive, int endInclusive) {
        return closedIntRange(startInclusive, endInclusive, ((startInclusive <= endInclusive) ? 1 : -1));
    }

    @Contract(pure = true) public static @NotNull IntStream closedIntRange(int startInclusive, int endInclusive, int step) {
        if((startInclusive <= endInclusive) && (step == 1)) return IntStream.rangeClosed(startInclusive, endInclusive);
        return StreamSupport.intStream(new ClosedRangeIterator(startInclusive, endInclusive, step), false);
    }

    public static @NotNull Stream<String> splitStream(@Nullable String val) {
        return splitStream(val, PGProperties.DEFAULT_LIST_SEPARATOR_PATTERN);
    }

    public static @NotNull Stream<String> splitStream(@Nullable String val, @RegExp @Language("RegExp") @NotNull @NonNls String regexp) {
        if(val == null) return Stream.empty();

        Matcher    m  = Regex.getMatcher(regexp, val);
        IntegerRef p1 = new IntegerRef(0);

        return streamWith(done -> {
            String string = null;
            if(p1.value == -1) {
                done.value = true;
            }
            else if(m.find()) {
                string   = val.substring(p1.value, m.start());
                p1.value = m.end();
            }
            else {
                string   = val.substring(p1.value);
                p1.value = -1;
            }
            return string;
        });
    }

    public static IntStream intRange(int startInclusive, int endExclusive) {
        return intRange(startInclusive, endExclusive, ((startInclusive <= endExclusive) ? 1 : -1));
    }

    public static IntStream intRange(int startInclusive, int endExclusive, int step) {
        if(startInclusive == endExclusive) return IntStream.empty();
        if((startInclusive < endExclusive) && (step == 1)) return IntStream.range(startInclusive, endExclusive);
        return StreamSupport.intStream(new RangeIterator(startInclusive, endExclusive, step), false);
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

    public static @NotNull <T> Stream<T> streamWith(@NotNull LambdaIteratorProvider<T> provider) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(new LambdaIterator<T>(provider), Spliterator.IMMUTABLE);
        return StreamSupport.stream(spliterator, false);
    }

    private enum LambdaIteratorState {Yes, No, Unkown}

    public interface LambdaIteratorProvider<T> {
        T provide(@NotNull BooleanRef done);
    }

    private static final class ArrayIterator<T> implements Iterator<CollectionItem<T>> {
        private final T[] array;
        private       int idx = 0;

        public ArrayIterator(T[] array) {
            this.array = array;
        }

        @Override public boolean hasNext() {
            return (idx < array.length);
        }

        @Contract(" -> new") @Override public @NotNull CollectionItem<T> next() {
            if(!hasNext()) throw new NoSuchElementException();
            int i = idx++;
            return new CollectionItem<>(i, array[i]);
        }
    }

    private static class ClosedRangeIterator extends RangeIterator {

        public ClosedRangeIterator(int start, int end, int step) {
            super(start, end, step);
        }

        @Override protected boolean hasNext() {
            return (up ? (next <= end) : (next >= end));
        }
    }

    private static final class LambdaIterator<T> implements Iterator<T> {
        private final LambdaIteratorProvider<T> provider;
        private       LambdaIteratorState       state = LambdaIteratorState.Unkown;
        private       T                         last  = null;

        public LambdaIterator(LambdaIteratorProvider<T> provider) {
            this.provider = provider;
        }

        @Override public boolean hasNext() {
            if(state == LambdaIteratorState.Unkown) {
                BooleanRef done = new BooleanRef(false);
                last  = provider.provide(done);
                state = (done.value ? LambdaIteratorState.No : LambdaIteratorState.Yes);
            }
            return (state == LambdaIteratorState.Yes);
        }

        @Override public T next() {
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

        @Override public boolean tryAdvance(IntConsumer action) {
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
