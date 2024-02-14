package com.projectgalen.lib.utils.text.box.data;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: BoxData.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: February 05, 2024
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

import com.projectgalen.lib.utils.enums.Align;
import com.projectgalen.lib.utils.text.box.Box.BoxType;
import com.projectgalen.lib.utils.text.box.Box.HdrDivType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.projectgalen.lib.utils.collections.PGArrays.ensureSize;

public final class BoxData {

    public static final String NULL_DEFAULT = "<null>";

    private final List<CharSequence[]> data;
    private final CharSequence[]       headers;
    private final Align[]              alignments;
    private final int[]                colWidths;
    private final int                  colCount;
    private final BoxType              boxType;
    private final HdrDivType           hdrDivType;

    public BoxData(@NotNull CharSequence data, boolean hasHeader, @NotNull BoxType boxType, @NotNull HdrDivType hdrDivType) {
        BoxStringParser foo = new BoxStringParser(data, hasHeader);
        this.data       = foo.getLines();
        this.headers    = foo.getHeaders();
        this.alignments = foo.getColumnAligns();
        this.colWidths  = foo.getColWidths();
        this.colCount   = foo.getColCount();
        this.boxType    = boxType;
        this.hdrDivType = hdrDivType;
    }

    public BoxData(String @NotNull [] headers, @NotNull List<String[]> data, Align[] alignments, BoxType boxType, HdrDivType hdrDivType) {
        this.data = new ArrayList<>();
        this.headers    = headers;
        this.alignments = alignments;
        this.boxType    = boxType;
        this.hdrDivType = hdrDivType;

        int   cc = headers.length;
        int[] cw = ensureSize(new int[16], cc);

        for(String[] r : data) {
            this.data.add(r);
            cc = Math.max(cc, r.length);
            cw = ensureSize(cw, r.length);
            for(int i = 0; i < r.length; ++i) {
                String str = r[i];
                if(str == null) r[i] = str = NULL_DEFAULT;
                cw[i] = Math.max(cw[i], str.length());
            }
        }

        this.colCount  = cc;
        this.colWidths = Arrays.copyOf(cw, cc);
    }

    public Align[] getAlignments() {
        return alignments;
    }

    public BoxType getBoxType() {
        return boxType;
    }

    public int getColCount() {
        return colCount;
    }

    public int[] getColWidths() {
        return colWidths;
    }

    public List<CharSequence[]> getData() {
        return data;
    }

    public HdrDivType getHdrDivType() {
        return hdrDivType;
    }

    public CharSequence[] getHeaders() {
        return headers;
    }
}
