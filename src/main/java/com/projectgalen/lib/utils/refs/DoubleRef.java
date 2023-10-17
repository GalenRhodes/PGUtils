package com.projectgalen.lib.utils.refs;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: DoubleRef.java
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

public class DoubleRef {
    public double value;

    public DoubleRef()                                          { }

    public DoubleRef(double initialValue)                       { value = initialValue; }

    public double add(double other)                             { return (value += other); }

    public double add(@NotNull DoubleRef other)                 { return add(other.value); }

    public double divide(double other)                          { return (value /= other); }

    public double divide(@NotNull DoubleRef other)              { return divide(other.value); }

    public @Override boolean equals(Object o)                   { return ((this == o) || ((o instanceof DoubleRef d) && (value == d.value))); }

    public @Override int hashCode()                             { return Objects.hash(value); }

    public double multiply(double other)                        { return (value *= other); }

    public double multiply(@NotNull DoubleRef other)            { return multiply(other.value); }

    public double setMax(double other)                          { return (value = Math.max(value, other)); }

    public double setMax(@NotNull DoubleRef other)              { return setMax(other.value); }

    public double setMin(double other)                          { return (value = Math.min(value, other)); }

    public double setMin(@NotNull DoubleRef other)              { return setMin(other.value); }

    public double subtract(double other)                        { return (value -= other); }

    public double subtract(@NotNull DoubleRef other)            { return subtract(other.value); }

    public static @NotNull DoubleRef getReference(double value) { return new DoubleRef(value); }
}
