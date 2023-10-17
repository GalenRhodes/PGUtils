package com.projectgalen.lib.utils.refs;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: ShortRef.java
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

public class ShortRef {
    public short value;

    public ShortRef()                                         { }

    public ShortRef(short initialValue)                       { value = initialValue; }

    public short add(short other)                             { return (value += other); }

    public short add(@NotNull ShortRef other)                 { return add(other.value); }

    public short divide(short other)                          { return (value /= other); }

    public short divide(@NotNull ShortRef other)              { return divide(other.value); }

    public @Override boolean equals(Object o)                 { return ((this == o) || ((o instanceof ShortRef s) && (value == s.value))); }

    public @Override int hashCode()                           { return Objects.hash(value); }

    public short multiply(short other)                        { return (value *= other); }

    public short multiply(@NotNull ShortRef other)            { return multiply(other.value); }

    public short setMax(short other)                          { return (value = (short)Math.max(value, other)); }

    public short setMax(@NotNull ShortRef other)              { return setMax(other.value); }

    public short setMin(short other)                          { return (value = (short)Math.min(value, other)); }

    public short setMin(@NotNull ShortRef other)              { return setMin(other.value); }

    public short subtract(short other)                        { return (value -= other); }

    public short subtract(@NotNull ShortRef other)            { return subtract(other.value); }

    public static @NotNull ShortRef getReference(short value) { return new ShortRef(value); }
}
