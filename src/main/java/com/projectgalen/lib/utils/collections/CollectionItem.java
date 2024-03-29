package com.projectgalen.lib.utils.collections;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: CollectionItem.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: June 02, 2023
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CollectionItem<T> {
    public final int index;
    public final T   item;

    public CollectionItem(int index, T item) {
        this.index = index;
        this.item  = item;
    }

    public @Override boolean equals(Object o) { return ((this == o) || ((o instanceof CollectionItem) && _equals((CollectionItem<?>)o))); }

    public int getIndex()                     { return index; }

    public T getItem()                        { return item; }

    public @Override int hashCode()           { return Objects.hash(index, item); }

    @Contract(pure = true)
    private boolean _equals(@NotNull CollectionItem<?> that) { return ((index == that.index) && Objects.equals(item, that.item)); }
}
