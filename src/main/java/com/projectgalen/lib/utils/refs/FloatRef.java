package com.projectgalen.lib.utils.refs;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: FloatRef.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: March 22, 2023
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

import java.util.Objects;

public class FloatRef {
    public float value;

    public FloatRef()                                         { }

    public FloatRef(float initialValue)                       { value = initialValue; }

    public float add(float other)                             { return (value += other); }

    public float add(@NotNull FloatRef other)                 { return add(other.value); }

    public float divide(float other)                          { return (value /= other); }

    public float divide(@NotNull FloatRef other)              { return divide(other.value); }

    public @Override boolean equals(Object o)                 { return ((this == o) || ((o instanceof FloatRef f) && (value == f.value))); }

    public @Override int hashCode()                           { return Objects.hash(value); }

    public float multiply(float other)                        { return (value *= other); }

    public float multiply(@NotNull FloatRef other)            { return multiply(other.value); }

    public float setMax(float other)                          { return (value = Math.max(value, other)); }

    public float setMax(@NotNull FloatRef other)              { return setMax(other.value); }

    public float setMin(float other)                          { return (value = Math.min(value, other)); }

    public float setMin(@NotNull FloatRef other)              { return setMin(other.value); }

    public float subtract(float other)                        { return (value -= other); }

    public float subtract(@NotNull FloatRef other)            { return subtract(other.value); }

    public static @NotNull FloatRef getReference(float value) { return new FloatRef(value); }
}
