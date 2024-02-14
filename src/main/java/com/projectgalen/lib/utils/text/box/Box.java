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
import com.projectgalen.lib.utils.text.Text;
import com.projectgalen.lib.utils.text.box.data.BoxData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.projectgalen.lib.utils.streams.Streams.arrayStream;
import static java.util.Arrays.stream;

public final class Box {

    private static final String[][] BOX_DATA = {/*@f0*/
        { "╔═╦╗\n",
          "║ ║║\n",
          "╠═╬╣\n",
          "╚═╩╝\n",
          "╔╗\n╚╝" },
        { "┌─┬┐\n",
          "│ ││\n",
          "├─┼┤\n",
          "└─┴┘\n",
          "┌┐\n└┘" },
        { "╓─╥╖\n",
          "║ ║║\n",
          "╟─╫╢\n",
          "╙─╨╜\n",
          "╓╖\n╙╜" },
        { "╒═╤╕\n",
          "│ ││\n",
          "╞═╪╡\n",
          "╘═╧╛\n",
          "╒╕\n╘╛" }
    };/*@f1*/

    private static final Align ALeft  = Align.Left;
    private static final Align ARight = Align.Right;

    private final List<CharSequence[]> data;
    private final CharSequence[]       headers;
    private final char[][]             boxElems;
    private final Align[]              alignments;
    private final int[]                colWidths;
    private final int[]                colIndexes;
    private final char[]               buffer;
    private final int                  bufferLen;
    private final boolean              hasHeaders;
    private final BoxType              boxType;
    private final HdrDivType           hdrDivType;

    private Box(@NotNull BoxData boxData) {
        this.headers    = boxData.getHeaders();
        this.data       = boxData.getData();
        this.boxType    = boxData.getBoxType();
        this.boxElems   = this.boxType.getBoxElements();
        this.hdrDivType = boxData.getHdrDivType();
        this.hasHeaders = (headers.length > 0);
        this.alignments = boxData.getAlignments();
        this.colWidths  = boxData.getColWidths();
        this.bufferLen  = (stream(colWidths).map(w -> (w + 3)).sum() + 2);
        this.buffer     = new char[bufferLen];
        this.colIndexes = new int[colWidths.length];

        arrayStream(colWidths, 0, colWidths.length - 1).forEach(w -> colIndexes[w.index + 1] = (colIndexes[w.index] + w.item + 3));
    }

    private void appendBuffer(@NotNull StringBuilder sb) {
        sb.append(buffer);
    }

    private @NotNull StringBuilder box() {
        if(!data.isEmpty() || hasHeaders) {
            StringBuilder sb = doHeaders(doBlank(new StringBuilder(), boxElems[0]));
            data.forEach(line -> doLine(sb, line));
            return doBlank(sb, boxElems[3]);
        }

        return new StringBuilder().append(boxElems[4]);
    }

    private @NotNull StringBuilder doBlank(@NotNull StringBuilder sb, char[] elements) {
        prep(elements);
        return sb.append(buffer);
    }

    private StringBuilder doHeaders(StringBuilder sb) {
        if(hasHeaders) {
            doLine(sb, headers);
            if(hdrDivType != HdrDivType.None) doBlank(sb, hdrDivType.getHeaderDivElements(boxType));
        }
        return sb;
    }

    private void doLine(@NotNull StringBuilder sb, CharSequence @NotNull [] line) {
        prep(boxElems[1]);
        fill(line);
        appendBuffer(sb);
    }

    private void fill(@NotNull CharSequence @NotNull ... line) {
        arrayStream(line).filter(x -> !x.item.isEmpty()).forEach(x -> write(Text.strip(x.item), x.index));
    }

    private void prep(char @NotNull [] elems) {
        Arrays.fill(buffer, elems[1]);
        buffer[0]             = elems[0];
        buffer[bufferLen - 2] = elems[3];
        buffer[bufferLen - 1] = elems[4];
        char ch = elems[2];
        arrayStream(colIndexes, 1, colIndexes.length).forEach(ci -> buffer[ci.item] = ch);
    }

    private void write(@NotNull CharSequence c, int i) {
        Align a  = ((i < alignments.length) ? alignments[i] : ALeft);
        int   cl = c.length();
        Text.getChars(c, 0, cl, buffer, (colIndexes[i] + 2) + ((a == ALeft) ? 0 : ((a == ARight) ? (colWidths[i] - cl) : ((colWidths[i] - cl) / 2))));
    }

    public static @NotNull StringBuilder box(String @NotNull [] headers, @NotNull List<String[]> data, Align @NotNull [] alignments) {
        return box(headers, data, alignments, BoxType.DoubleBoth, HdrDivType.Single);
    }

    public static @NotNull StringBuilder box(String @NotNull [] headers, @NotNull List<String[]> data, Align @NotNull [] alignments, @NotNull BoxType boxType, @NotNull HdrDivType hdrDivType) {
        return box(new BoxData(headers, data, alignments, boxType, hdrDivType));
    }

    public static @NotNull StringBuilder box(@NotNull CharSequence data, boolean hasHeader, @NotNull BoxType boxType, @NotNull HdrDivType hdrDivType) {
        return box(new BoxData(data, hasHeader, boxType, hdrDivType));
    }

    private static @NotNull StringBuilder box(@NotNull BoxData boxData) {
        return new Box(boxData).box();
    }

    public enum BoxType {
        DoubleBoth, SingleBoth, SingleAcross, DoubleAcross;

        public char @NotNull [] @NotNull [] getBoxElements() {
            char[][] ch = new char[5][5];
            String[] str = switch(this) {
                case DoubleBoth -> BOX_DATA[0];
                case SingleBoth -> BOX_DATA[1];
                case SingleAcross -> BOX_DATA[2];
                case DoubleAcross -> BOX_DATA[3];
            };
            for(int i = 0; i < 5; ++i) str[i].getChars(0, 5, ch[i], 0);
            return ch;
        }
    }

    public enum HdrDivType {
        None, Single, Double;

        public @Contract(value = "_ -> new", pure = true) char @NotNull [] getHeaderDivElements(@NotNull BoxType boxType) {
            char[] ch = new char[5];
            BOX_DATA[switch(boxType) {
                case DoubleBoth, SingleAcross -> ((this == HdrDivType.Single) ? 2 : 0);
                case SingleBoth, DoubleAcross -> ((this == HdrDivType.Single) ? 1 : 3);
            }][2].getChars(0, 5, ch, 0);
            return ch;
        }
    }
}
