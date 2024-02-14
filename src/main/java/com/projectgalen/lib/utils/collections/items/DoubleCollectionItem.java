package com.projectgalen.lib.utils.collections.items;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: DoubleCollectionItem.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: February 10, 2024
//
// Copyright © 2024 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import java.util.Objects;

public class DoubleCollectionItem {
    public final int    index;
    public final double item;

    public DoubleCollectionItem(int index, double item) {
        this.index = index;
        this.item  = item;
    }

    public @Override boolean equals(Object o) { return ((this == o) || ((o instanceof DoubleCollectionItem i) && (index == i.index) && (item == i.item))); }

    public int getIndex()                     { return index; }

    public double getItem()                   { return item; }

    public @Override int hashCode()           { return Objects.hash(index, item); }
}
