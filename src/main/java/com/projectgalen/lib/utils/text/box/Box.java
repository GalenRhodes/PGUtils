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
import com.projectgalen.lib.utils.text.box.data.BoxData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Box {

    /**
     * I know there's a lot of duplication here but hopefully that will make life easier.
     */
    private static final char[][][] BOX_CHARS = {/*@f0*/
        { { '╔', '═', '╦', '═', '╗' },
          { '║', ' ', '║', ' ', '║' },
          { '╠', '═', '╬', '═', '╣' },
          { '║', ' ', '║', ' ', '║' },
          { '╚', '═', '╩', '═', '╝' },
          { '╔', '╗', '\n', '╚', '╝' } },

        { { '┌', '─', '┬', '─', '┐' },
          { '│', ' ', '│', ' ', '│' },
          { '├', '─', '┼', '─', '┤' },
          { '│', ' ', '│', ' ', '│' },
          { '└', '─', '┴', '─', '┘' },
          { '┌', '┐', '\n', '└', '┘' } },

        { { '╓', '─', '╥', '─', '╖' },
          { '║', ' ', '║', ' ', '║' },
          { '╟', '─', '╫', '─', '╢' },
          { '║', ' ', '║', ' ', '║' },
          { '╙', '─', '╨', '─', '╜' },
          { '╓', '╖', '\n', '╙', '╜' } },

        { { '╒', '═', '╤', '═', '╕' },
          { '│', ' ', '│', ' ', '│' },
          { '╞', '═', '╪', '═', '╡' },
          { '│', ' ', '│', ' ', '│' },
          { '╘', '═', '╧', '═', '╛' },
          { '╒', '╕', '\n', '╘', '╛' } }
    };/*@f1*/

    private Box() { }

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
        char[][]      boxElems       = boxData.getBoxType().getBoxArray();
        char[]        boxHdrDivElems = boxData.getHdrDivType().getHeaderDivider(boxData.getBoxType());
        StringBuilder sb             = new StringBuilder();

        if(boxData.getData().isEmpty() && (boxData.getHeaders().length == 0)) return sb.append(boxElems[5]);

        return sb;
    }

    public enum BoxType {
        DoubleBoth, SingleBoth, SingleAcross, DoubleAcross;

        public char[][] getBoxArray() {
            return switch(this) {/*@f0*/
                case DoubleBoth   -> BOX_CHARS[0];
                case SingleBoth   -> BOX_CHARS[1];
                case SingleAcross -> BOX_CHARS[2];
                case DoubleAcross -> BOX_CHARS[3];
            };/*@f1*/
        }
    }

    public enum HdrDivType {
        None, Single, Double;

        public char[] getHeaderDivider(@NotNull BoxType boxTypeIndex) {
            return BOX_CHARS[switch(boxTypeIndex) {
                case DoubleBoth, SingleAcross -> ((this == HdrDivType.Single) ? 2 : 0);
                case SingleBoth, DoubleAcross -> ((this == HdrDivType.Single) ? 1 : 3);
            }][2];
        }
    }
}
