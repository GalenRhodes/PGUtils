package com.projectgalen.lib.utils.collections.items.iterators;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: ArrayIterator.java
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

import com.projectgalen.lib.utils.collections.items.CollectionItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayIterator<T> implements Iterator<CollectionItem<T>> {
    private final T @NotNull [] array;
    private       int           idx;
    private final int           endIndex;

    public ArrayIterator(T @NotNull [] array) {
        this(array, 0, array.length);
    }

    public ArrayIterator(T @NotNull [] array, int startIndex, int endIndex) {
        this.array    = array;
        this.idx      = startIndex;
        this.endIndex = endIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public @Override boolean hasNext() {
        return (idx < endIndex);
    }

    public @Contract(" -> new") @Override @NotNull CollectionItem<T> next() {
        if(!hasNext()) throw new NoSuchElementException();
        int i = idx++;
        return new CollectionItem<>(i, array[i]);
    }
}