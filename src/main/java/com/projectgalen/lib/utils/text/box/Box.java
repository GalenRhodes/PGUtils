package com.projectgalen.lib.utils.text.box;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: Box.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: December 05, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import com.projectgalen.lib.utils.enums.Align;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class Box {
    private Box() { }

    public static void box(@NotNull Writer writer, @NotNull Reader reader) throws IOException {
        Align   alignment = Align.Left;
        int     width     = 80;
        int     tabWidth  = 4;
        boolean stretch   = true;

        int            lineLimit = (width - 4);
        int            last      = 0;
        int            maxWidth  = 0;
        BufferedReader bReader   = ((reader instanceof BufferedReader r) ? r : new BufferedReader(reader));
        StringBuilder  sb        = new StringBuilder();
        List<String>   lines     = new ArrayList<>();
        int            iChar     = bReader.read();
        int            currWidth = 0;

        while(iChar >= 0) {
            if((iChar == '\r') || (iChar == '\n')) {

            }
        }
        if(!sb.isEmpty()) lines.add(sb.toString());
    }
}
