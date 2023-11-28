package com.projectgalen.lib.utils.helpers;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: IfTools.java
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
import java.util.function.Predicate;

public interface IfTools {

    default <O> void ifDo(@Nullable O obj, @NotNull Predicate<O> predicate, @NotNull Consumer<O> trueConsumer, @NotNull Consumer<O> falseConsumer) {
        Optional.ofNullable(obj).filter(predicate).ifPresentOrElse(trueConsumer, () -> falseConsumer.accept(obj));
    }

    default <O> void ifDo(@Nullable O obj, @NotNull Predicate<O> predicate, @NotNull Consumer<O> trueConsumer) {
        ifDo(obj, predicate, trueConsumer, o -> { });
    }
}
