package com.projectgalen.lib.utils.text.box.data;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: BoxStringParser.java
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BoxStringParser {

    private final Pattern        colPattern = Pattern.compile("\\t");
    private final List<String>   temp       = new ArrayList<>();
    private final List<String[]> lines      = new ArrayList<>();
    private       String[]       headers    = new String[0];
    private       int[]          colWidths  = new int[20];
    private       int            colCount   = 0;
    private       int            last       = 0;

    public BoxStringParser(@NotNull CharSequence data, boolean hasHeader) {
        Matcher m = Pattern.compile("\\R").matcher(data);
        Arrays.fill(colWidths, 0);

        if(m.find()) {
            if(hasHeader) {
                headers  = splitColumns(get(data, m.start(), m.end()));
                colCount = headers.length;
                updateColumnWidths(headers);
                while(m.find()) doNext(get(data, m.start(), m.end()));
            }
            else {
                do doNext(get(data, m.start(), m.end())); while(m.find());
            }
            doLast(data);
        }
    }

    public int getColCount() {
        return colCount;
    }

    public @Contract(value = " -> new", pure = true) int @NotNull [] getColWidths() {
        return Arrays.copyOf(colWidths, colCount);
    }

    public Align @NotNull [] getColumnAlignments() {
        Align[] alignments = new Align[colCount];
        Arrays.fill(alignments, Align.Left);
        for(int i = 0, j = Math.min(colCount, headers.length); i < j; ++i) {
            String header = headers[i];
            if(!header.isEmpty()) alignments[i] = switch(header.charAt(0)) {/*@f0*/
                case '<' -> { headers[i] = header.substring(1); yield Align.Left;   }
                case '>' -> { headers[i] = header.substring(1); yield Align.Right;  }
                case '^' -> { headers[i] = header.substring(1); yield Align.Center; }
                default  -> Align.Left;
            };/*@f1*/
        }
        return alignments;
    }

    public String[] getHeaders() {
        return headers;
    }

    public List<String[]> getLines() {
        return lines;
    }

    private void checkColWidthsArray(int ll) {
        if(colWidths.length < ll) {
            int l = (colWidths.length * 2);
            while(l < ll) l *= 2;
            colWidths = Arrays.copyOf(colWidths, l);
        }
    }

    private void doLast(@NotNull CharSequence data) {
        int          l = data.length();
        CharSequence s = get(data, l, l);
        if(!s.isEmpty()) doNext(s);
    }

    private void doNext(@NotNull CharSequence s) {
        String[] l = splitColumns(s);
        if(l.length > 0) {
            lines.add(l);
            colCount = Math.max(colCount, l.length);
            updateColumnWidths(l);
        }
    }

    private @NotNull CharSequence get(@NotNull CharSequence data, int a, int b) {
        CharSequence s = data.subSequence(last, a);
        last = b;
        return s;
    }

    private String @NotNull [] splitColumns(@NotNull CharSequence line) {
        try {
            Matcher m    = colPattern.matcher(line);
            int     last = 0;

            while(m.find()) {
                temp.add(line.subSequence(last, m.start()).toString().strip());
                last = m.end();
            }
            temp.add(line.subSequence(last, line.length()).toString().strip());

            return temp.toArray(new String[0]);
        }
        finally {
            temp.clear();
        }
    }

    private void updateColumnWidths(String @NotNull [] line) {
        checkColWidthsArray(line.length);
        for(int i = 0; i < line.length; ++i) colWidths[i] = Math.max(colWidths[i], line[i].length());
    }
}
