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

import java.util.Objects;

public class IntegerRef {
    public int value;

    public IntegerRef() { }

    public IntegerRef(int initialValue) { value = initialValue; }

    @Override
    public boolean equals(Object o) {
        return ((this == o) || ((o instanceof IntegerRef) && (value == ((IntegerRef)o).value)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
