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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Null implements Cloneable {
    private Null() { }

    public static Null NULL() {
        return NullHolder.INSTANCE;
    }

    public static <T> T get(@NotNull Object o) {
        if(NULL().equals(o)) return null;
        //noinspection unchecked
        return (T) o;
    }

    public static @NotNull Object set(@Nullable Object o) {
        return U.ifNull(o, NULL());
    }

    public @Override int hashCode() {
        return 0;
    }

    public @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override boolean equals(Object obj) {
        return (obj == NULL());
    }

    protected @Override Object clone() throws CloneNotSupportedException {
        return NULL();
    }

    public @Override String toString() {
        return "null";
    }

    private static final class NullHolder {
        private static final Null INSTANCE = new Null();
    }
}
