package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Null.java
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

import com.projectgalen.lib.utils.delegates.ThrowingConsumer;
import com.projectgalen.lib.utils.delegates.ThrowingFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({ "SameParameterValue", "unchecked" })
public final class Null implements Cloneable {
    private Null() { }

    public @Override boolean equals(Object obj) {
        return (obj instanceof Null);
    }

    public @Override int hashCode() {
        return 0;
    }

    @Contract(pure = true)
    public @Override @NotNull String toString() {
        return "null";
    }

    @Contract(pure = true)
    protected @Override @Unmodifiable Object clone() {
        return NULL();
    }

    public static Null NULL() {
        return NullHolder.INSTANCE;
    }

    public static <P> void doIf(@Nullable P value, @NotNull Runnable whenNullDelegate, @NotNull Consumer<P> whenNotNullDelegate) {
        if(value == null) whenNullDelegate.run();
        else whenNotNullDelegate.accept(value);
    }

    public static <P> void doIfNotNull(@Nullable P value, @NotNull Consumer<P> delegate) {
        if(value != null) delegate.accept(value);
    }

    public static <T> void doIfNotNullThrows(@Nullable T value, @NotNull ThrowingConsumer<T, Exception> delegate) throws Exception {
        if(value != null) delegate.accept(value);
    }

    public static <T> @Nullable T get(@Nullable Object o) {
        return (((o == null) || (o instanceof Null)) ? null : (T)o);
    }

    public static <P, R> R getIf(@Nullable P value, @NotNull Supplier<R> whenNullDelegate, @NotNull Function<P, R> whenNotNullDelegate) {
        return ((value == null) ? whenNullDelegate.get() : whenNotNullDelegate.apply(value));
    }

    public static <P, R> R getIfNotNull(@Nullable P value, @NotNull Function<P, R> delegate) {
        return getIfNotNull(value, null, delegate);
    }

    public static <P, R> R getIfNotNull(@Nullable P value, @Nullable R defaultValue, @NotNull Function<P, R> delegate) {
        return ((value == null) ? defaultValue : delegate.apply(value));
    }

    public static <P, R> R getIfNotNullThrows(@Nullable P value, @NotNull ThrowingFunction<P, R, Exception> delegate) throws Exception {
        return getIfNotNullThrows(value, null, delegate);
    }

    public static <P, R> R getIfNotNullThrows(@Nullable P value, @Nullable R defaultValue, @NotNull ThrowingFunction<P, R, Exception> delegate) throws Exception {
        return ((value == null) ? defaultValue : delegate.apply(value));
    }

    @Deprecated(forRemoval = true)
    public static @NotNull <T> T ifNull(@Nullable T obj, @NotNull T defaultValue) {
        return Objects.requireNonNullElse(obj, defaultValue);
    }

    @Contract("null, _ -> fail; !null, _ -> param1")
    public static <T, E extends Exception> @NotNull T requireOrThrow(T obj, Supplier<E> supplier) throws E {
        if(obj == null) throw supplier.get();
        return obj;
    }

    public static @NotNull Object set(@Nullable Object o) {
        return Objects.requireNonNullElse(o, NULL());
    }

    private static final class NullHolder {
        private static final Null INSTANCE = new Null();
    }
}
