package com.projectgalen.lib.utils.text;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: CodePointEnumerator.java
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

public interface CodePointEnumerator {
    int getNextIdx();

    boolean hasNext();

    int next();

    static @NotNull CodePointEnumerator getForward(@NotNull CharSequence cs) {
        return getForward(cs, 0, cs.length());
    }

    static @NotNull CodePointEnumerator getForward(@NotNull CharSequence cs, int startIdx, int endIdx) {
        return new CodePointForwardEnumerator(cs, startIdx, endIdx);
    }

    static @NotNull CodePointEnumerator getReverse(@NotNull CharSequence cs) {
        return getReverse(cs, cs.length() - 1, -1);
    }

    static @NotNull CodePointEnumerator getReverse(@NotNull CharSequence cs, int startIdx, int endIdx) {
        return new CodePointReverseEnumerator(cs, startIdx, endIdx);
    }
}
