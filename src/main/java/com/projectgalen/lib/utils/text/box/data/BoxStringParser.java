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
import com.projectgalen.lib.utils.text.regex.Regex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static com.projectgalen.lib.utils.collections.PGArrays.*;
import static com.projectgalen.lib.utils.streams.Streams.arrayStream;
import static com.projectgalen.lib.utils.text.Text.split;

public final class BoxStringParser {

    private final CharSequence         input;
    private final List<CharSequence[]> lines;
    private final boolean              hasHdr;
    private       Align[]              columnAligns = null;
    private       CharSequence[]       hdrs         = new CharSequence[0];
    private       int[]                colWidths    = new int[20];
    private       int                  startIdx     = 0;

    public BoxStringParser(@NotNull CharSequence inputString, boolean hasHeader) {
        lines  = new ArrayList<>();
        input  = inputString;
        hasHdr = hasHeader;

        int cCount = parse();

        columnAligns = overlay(Stream.of(hdrs).map(Align::toAlign).toArray(Align[]::new), createAndFill(cCount, Align.Left));
        colWidths    = Arrays.copyOf(colWidths, cCount);
    }

    public BoxStringParser(@NotNull List<String[]> rows, Align @NotNull [] alignments, String @NotNull [] headers) {
        lines = new ArrayList<>();
        hdrs  = headers;
        input = "";

        int cCount = hdrs.length;
        hasHdr = (cCount > 0);

        if(hasHdr) {
            colWidths = ensureSize(colWidths, cCount);
            for(int i = 0; i < cCount; ++i) colWidths[i] = hdrs[i].length();
        }

        for(CharSequence[] line : rows) {
            lines.add(line);
            cCount    = Math.max(cCount, line.length);
            colWidths = ensureSize(colWidths, cCount);
            for(int i = 0; i < line.length; ++i) colWidths[i] = Math.max(colWidths[i], line[i].length());
        }

        colWidths    = copyOf(colWidths, cCount, 0);
        columnAligns = copyOf(alignments, cCount, Align.Left);
    }

    public int getColCount() {
        return colWidths.length;
    }

    public int @NotNull [] getColWidths() {
        return colWidths;
    }

    public Align @NotNull [] getColumnAligns() {
        return columnAligns;
    }

    public CharSequence[] getHeaders() {
        return hdrs;
    }

    public List<CharSequence[]> getLines() {
        return lines;
    }

    public boolean hasHeader() {
        return (hasHdr && (hdrs.length > 0));
    }

    private int doLine(int endIdx, int nextIdx) {
        String[] line = getNext(endIdx, nextIdx);
        if(line.length > 0) {
            lines.add(line);
            colWidths = overlay(arrayStream(line).mapToInt(i -> Math.max(i.item.length(), colWidths[i.index])).toArray(), ensureSize(colWidths, line.length, 0));
        }
        return line.length;
    }

    private int doNext(@NotNull Matcher matcher) {
        return doLine(matcher.start(), matcher.end());
    }

    private String @NotNull [] getNext(int endIdx, int nextIdx) {
        String[] line = split(input.subSequence(startIdx, endIdx), "\\t");
        startIdx = nextIdx;
        return line;
    }

    private int parse() {
        Matcher matcher = Regex.getMatcher("\\R", input);

        if(matcher.find()) {
            int cCount = 0;

            if(hasHdr) {
                String[] rawHeaders = getNext(matcher.start(), matcher.end());
                cCount = rawHeaders.length;

                if(cCount > 0) {
                    colWidths    = ensureSize(colWidths, cCount);
                    columnAligns = new Align[cCount];
                    hdrs         = new String[cCount];

                    for(int i = 0; i < cCount; ++i) parseHeader(i, rawHeaders[i]);
                }

                while(matcher.find()) cCount = Math.max(cCount, doNext(matcher));
            }
            else {
                do cCount = Math.max(cCount, doNext(matcher)); while(matcher.find());
            }

            cCount = Math.max(cCount, doLine(input.length(), input.length()));
            if(columnAligns == null) columnAligns = createAndFill(cCount, Align.Left);
            return cCount;
        }

        return 0;
    }

    private void parseHeader(int idx, @NotNull String header) {
        switch(header.isEmpty() ? '?' : header.charAt(0)) {
            case '<' -> {
                columnAligns[idx] = Align.Left;
                hdrs[idx]         = header.substring(1);
            }
            case '>' -> {
                columnAligns[idx] = Align.Right;
                hdrs[idx]         = header.substring(1);
            }
            case '^' -> {
                columnAligns[idx] = Align.Center;
                hdrs[idx]         = header.substring(1);
            }
            default -> columnAligns[idx] = Align.Left;
        }

        colWidths[idx] = hdrs[idx].length();
    }
}
