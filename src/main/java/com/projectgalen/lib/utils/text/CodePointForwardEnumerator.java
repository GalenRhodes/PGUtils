package com.projectgalen.lib.utils.text;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: CodePointForwardEnumerator.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: February 14, 2024
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

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

final class CodePointForwardEnumerator implements CodePointEnumerator {
    private       int          nextIdx;
    private final int          endIdx;
    private final CharSequence cs;

    public CodePointForwardEnumerator(@NotNull CharSequence cs, int startIdx, int endIdx) {
        this.cs      = cs;
        this.nextIdx = startIdx;
        this.endIdx  = endIdx;
    }

    public int getNextIdx() {
        return nextIdx;
    }

    public boolean hasNext() {
        return (nextIdx < endIdx);
    }

    public int next() {
        if(nextIdx < endIdx) {
            char c1 = cs.charAt(nextIdx++);

            if(Character.isHighSurrogate(c1) && (nextIdx < endIdx)) {
                char c2 = cs.charAt(nextIdx);

                if(Character.isLowSurrogate(c2)) {
                    ++nextIdx;
                    return Character.toCodePoint(c1, c2);
                }
            }

            return c1;
        }

        throw new NoSuchElementException();
    }
}
