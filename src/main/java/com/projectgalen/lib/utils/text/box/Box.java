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
import com.projectgalen.lib.utils.text.CharArraySequence;
import com.projectgalen.lib.utils.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public final class Box {

    /**
     * I know there's a lot of duplication here but hopefully that will make life easier.
     */
    private static final char[][][] BOX_CHARS = {/*@f0*/
        { { '╔', '═', '╦', '═', '╗' },
          { '║', ' ', '║', ' ', '║' },
          { '╠', '═', '╬', '═', '╣' },
          { '║', ' ', '║', ' ', '║' },
          { '╚', '═', '╩', '═', '╝' } },

        { { '┌', '─', '┬', '─', '┐' },
          { '│', ' ', '│', ' ', '│' },
          { '├', '─', '┼', '─', '┤' },
          { '│', ' ', '│', ' ', '│' },
          { '└', '─', '┴', '─', '┘' } },

        { { '╓', '─', '╥', '─', '╖' },
          { '║', ' ', '║', ' ', '║' },
          { '╟', '─', '╫', '─', '╢' },
          { '║', ' ', '║', ' ', '║' },
          { '╙', '─', '╨', '─', '╜' } },

        { { '╒', '═', '╤', '═', '╕' },
          { '│', ' ', '│', ' ', '│' },
          { '╞', '═', '╪', '═', '╡' },
          { '│', ' ', '│', ' ', '│' },
          { '╘', '═', '╧', '═', '╛' } }
    };/*@f1*/

    private Box() { }

    public static @NotNull StringBuilder box(String @NotNull [] headers, @NotNull List<String[]> data, Align @NotNull [] alignments) {
        BoxType           boxType    = BoxType.DoubleBoth;
        HeaderDividerType hdrDivType = HeaderDividerType.Single;

        return box(headers, data, alignments, boxType, hdrDivType);
    }

    public static @NotNull StringBuilder box(String @NotNull [] headers, @NotNull List<String[]> data, Align @NotNull [] alignments, @NotNull BoxType boxType, @NotNull HeaderDividerType hdrDivType) {
        StringBuilder sb              = new StringBuilder();
        char[][]      boxTypeArray    = boxType.getBoxArray();
        char[]        hdrDivTypeArray = hdrDivType.getHeaderDivider(boxType);
        int           maxColumns      = headers.length;
        int[]         columnWidths    = new int[maxColumns];
        int           maxWidth        = 0;

        for(int i = 0; i < headers.length; ++i) {
            headers[i]      = Text.escape(ofNullable(headers[i]).orElse(""));
            columnWidths[i] = headers[i].length();
        }

        for(String[] row : data) {
            maxColumns = Math.max(maxColumns, row.length);
            if(maxColumns > columnWidths.length) columnWidths = Arrays.copyOf(columnWidths, maxColumns);

            for(int i = 0; i < row.length; ++i) {
                row[i]          = Text.escape(ofNullable(row[i]).orElse(""));
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }

        maxWidth = 1;
        for(int w : columnWidths) maxWidth += (w + 3);

        alignments = Arrays.copyOf(alignments, maxColumns);
        for(int i = 0; i < alignments.length; ++i) alignments[i] = ofNullable(alignments[i]).orElse(Align.Left);

        char[]                         buffer = new char[maxWidth];
        CharArraySequence              cs     = CharArraySequence.noCopyValueOf(buffer);
        Function<char[], CharSequence> f      = b -> cs;

        sb.append(f.apply(prepBuffer(buffer, maxWidth, columnWidths, boxTypeArray[0]))).append('\n');

        sb.append(f.apply(drawColumns(prepBuffer(buffer, maxWidth, columnWidths, boxTypeArray[1]), columnWidths, alignments, headers))).append('\n');

        sb.append(f.apply(prepBuffer(buffer, maxWidth, columnWidths, hdrDivTypeArray))).append('\n');

        for(String[] row : data) {
            sb.append(f.apply(drawColumns(prepBuffer(buffer, maxWidth, columnWidths, boxTypeArray[1]), columnWidths, alignments, row))).append('\n');
        }

        sb.append(f.apply(prepBuffer(buffer, maxWidth, columnWidths, boxTypeArray[4])));

        return sb;
    }

    private static int drawColumn(char @NotNull [] buffer, int bufferIndex, @NotNull String columnText, int columnWidth, @NotNull Align columnAlignment) {
        final int strLen = columnText.length();
        switch(columnAlignment) {
            case Left -> columnText.getChars(0, strLen, buffer, bufferIndex);
            case Right -> columnText.getChars(0, strLen, buffer, bufferIndex + (columnWidth - strLen));
            case Center -> columnText.getChars(0, strLen, buffer, bufferIndex + ((columnWidth - strLen) / 2));
        }
        return (bufferIndex + columnWidth + 3);
    }

    @Contract("_, _, _, _ -> param1") private static char @NotNull [] drawColumns(char @NotNull [] buffer, int @NotNull [] columnWidths, Align @NotNull [] alignments, String @NotNull [] row) {
        for(int i = 0, j = 2; i < row.length; ++i) j = drawColumn(buffer, j, row[i], columnWidths[i], alignments[i]);
        return buffer;
    }

    @Contract("_, _, _, _ -> param1") private static char @NotNull [] prepBuffer(char @NotNull [] buffer, int maxWidth, int @NotNull [] columnWidths, char @NotNull [] boxType) {
        Arrays.fill(buffer, boxType[1]);
        buffer[0] = boxType[0];
        for(int i = 0, j = 0; i < columnWidths.length; ++i) buffer[(j = (j + columnWidths[i] + 3))] = boxType[2];
        buffer[maxWidth - 1] = boxType[4];
        return buffer;
    }

    public enum BoxType {
        DoubleBoth, SingleBoth, SingleAcross, DoubleAcross;

        public char[][] getBoxArray() {
            return switch(this) {
                case DoubleBoth -> BOX_CHARS[0];
                case SingleBoth -> BOX_CHARS[1];
                case SingleAcross -> BOX_CHARS[2];
                case DoubleAcross -> BOX_CHARS[3];
            };
        }
    }

    public enum HeaderDividerType {
        Single, Double;

        public char[] getHeaderDivider(@NotNull BoxType boxTypeIndex) {
            return BOX_CHARS[switch(boxTypeIndex) {
                case DoubleBoth, SingleAcross -> ((this == HeaderDividerType.Single) ? 2 : 0);
                case SingleBoth, DoubleAcross -> ((this == HeaderDividerType.Single) ? 1 : 3);
            }][2];
        }
    }
}
