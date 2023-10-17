package com.projectgalen.lib.utils.refs;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: ByteRef.java
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

public class ByteRef {
    public byte value;

    public ByteRef()                                        { }

    public ByteRef(byte initialValue)                       { value = initialValue; }

    public byte add(byte other)                             { return (value += other); }

    public byte add(@NotNull ByteRef other)                 { return add(other.value); }

    public byte divide(byte other)                          { return (value /= other); }

    public byte divide(@NotNull ByteRef other)              { return divide(other.value); }

    public @Override boolean equals(Object o)               { return ((this == o) || ((o instanceof ByteRef b) && (value == b.value))); }

    public @Override int hashCode()                         { return Objects.hash(value); }

    public byte multiply(byte other)                        { return (value *= other); }

    public byte multiply(@NotNull ByteRef other)            { return multiply(other.value); }

    public byte setMax(byte other)                          { return (value = (byte)Math.max(value, other)); }

    public byte setMax(@NotNull ByteRef other)              { return setMax(other.value); }

    public byte setMin(byte other)                          { return (value = (byte)Math.min(value, other)); }

    public byte setMin(@NotNull ByteRef other)              { return setMin(other.value); }

    public byte subtract(byte other)                        { return (value -= other); }

    public byte subtract(@NotNull ByteRef other)            { return subtract(other.value); }

    public static @NotNull ByteRef getReference(byte value) { return new ByteRef(value); }
}
