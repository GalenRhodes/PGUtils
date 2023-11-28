package com.projectgalen.lib.utils.helpers;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: NullTools.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 13, 2023
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface NullTools extends IfTools {

    default <O, R> R from(@Nullable O obj, @NotNull Function<O, R> function, @NotNull Supplier<R> defaultSupplier) {
        return Optional.ofNullable(obj).map(function).orElseGet(defaultSupplier);
    }

    default <O, P, R> R from2(@Nullable O obj, @NotNull Function<O, P> mapFunc1, @NotNull Function<P, R> mapFunc2, @NotNull Supplier<R> defaultSupplier) {
        return Optional.ofNullable(obj).map(mapFunc1).map(mapFunc2).orElseGet(defaultSupplier);
    }

    default <O, P, R> R from2V(@Nullable O obj, @NotNull Function<O, P> mapFunc1, @NotNull Function<P, R> mapFunc2, R defaultValue) {
        return from2(obj, mapFunc1, mapFunc2, () -> defaultValue);
    }

    default <O, R> R fromV(@Nullable O obj, @NotNull Function<O, R> function, R defaultValue) {
        return from(obj, function, () -> defaultValue);
    }

    default <O> void with(@Nullable O obj, @NotNull Consumer<O> consumer) {
        Optional.ofNullable(obj).ifPresent(consumer);
    }

    default <O, P> void with2(@Nullable O obj, @NotNull Function<O, P> function, @NotNull Consumer<P> consumer) {
        Optional.ofNullable(obj).map(function).ifPresent(consumer);
    }

    default <O, P1, P2> void with3(@Nullable O obj, @NotNull Function<O, P1> mapFunction1, @NotNull Function<P1, P2> mapFunction2, @NotNull Consumer<P2> consumer) {
        Optional.ofNullable(obj).map(mapFunction1).map(mapFunction2).ifPresent(consumer);
    }
}
