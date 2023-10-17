package com.projectgalen.lib.utils.refs;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: IntegerRef.java
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

public class IntegerRef {
    public int value;

    public IntegerRef()                                       { }

    public IntegerRef(int initialValue)                       { value = initialValue; }

    public int add(int other)                                 { return (value += other); }

    public int add(@NotNull IntegerRef other)                 { return add(other.value); }

    public int divide(int other)                              { return (value /= other); }

    public int divide(@NotNull IntegerRef other)              { return divide(other.value); }

    public @Override boolean equals(Object o)                 { return ((this == o) || ((o instanceof IntegerRef i) && (value == i.value))); }

    public @Override int hashCode()                           { return Objects.hash(value); }

    public int multiply(int other)                            { return (value *= other); }

    public int multiply(@NotNull IntegerRef other)            { return multiply(other.value); }

    public int setMax(int other)                              { return (value = Math.max(value, other)); }

    public int setMax(@NotNull IntegerRef other)              { return setMax(other.value); }

    public int setMin(int other)                              { return (value = Math.min(value, other)); }

    public int setMin(@NotNull IntegerRef other)              { return setMin(other.value); }

    public int subtract(int other)                            { return (value -= other); }

    public int subtract(@NotNull IntegerRef other)            { return subtract(other.value); }

    public static @NotNull IntegerRef getReference(int value) { return new IntegerRef(value); }
}
