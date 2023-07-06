package com.projectgalen.lib.utils.delegates;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Comparing.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: July 06, 2023
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

import java.util.Optional;
import java.util.function.BiFunction;

public interface Comparing<T> extends BiFunction<T, T, Integer> {

    default boolean e(T a, T b)                  { return Optional.ofNullable(apply(a, b)).orElse(1) == 0; }

    default boolean equal(T a, T b)              { return Optional.ofNullable(apply(a, b)).orElse(1) == 0; }

    default boolean greaterThan(T a, T b)        { return Optional.ofNullable(apply(a, b)).orElse(-1) > 0; }

    default boolean greaterThanOrEqual(T a, T b) { return Optional.ofNullable(apply(a, b)).orElse(-1) >= 0; }

    default boolean gt(T a, T b)                 { return Optional.ofNullable(apply(a, b)).orElse(-1) > 0; }

    default boolean gte(T a, T b)                { return Optional.ofNullable(apply(a, b)).orElse(-1) >= 0; }

    default boolean lessThan(T a, T b)           { return Optional.ofNullable(apply(a, b)).orElse(1) < 0; }

    default boolean lessThanOrEqual(T a, T b)    { return Optional.ofNullable(apply(a, b)).orElse(1) <= 0; }

    default boolean lt(T a, T b)                 { return Optional.ofNullable(apply(a, b)).orElse(1) < 0; }

    default boolean lte(T a, T b)                { return Optional.ofNullable(apply(a, b)).orElse(1) <= 0; }
}
