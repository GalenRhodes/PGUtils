package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PRef.java
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class PRef<T> {

    protected final @NotNull      Class<T> clazz;
    protected @UnknownNullability T        value;

    public PRef(@NotNull Class<T> clazz) {
        this.value = null;
        this.clazz = clazz;
    }

    public PRef(@NotNull Class<T> clazz, @UnknownNullability T initialValue) {
        this.value = initialValue;
        this.clazz = clazz;
    }

    @NotNull
    public Class<T> getValueClass() {
        return clazz;
    }

    @UnknownNullability
    public T getValue() {
        return value;
    }

    public void setValue(@UnknownNullability T value) {
        this.value = value;
    }
}
