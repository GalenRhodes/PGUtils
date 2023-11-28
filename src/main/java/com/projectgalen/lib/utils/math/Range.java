package com.projectgalen.lib.utils.math;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Range.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: February 21, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import com.projectgalen.lib.utils.PGResourceBundle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

@SuppressWarnings({ "SpellCheckingInspection", "unused" })
public final class Range {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    public final int start;
    public final int end;
    public final int length;

    public Range(int start, int length) {
        if(start < 0) throw new IllegalArgumentException(msgs.getString("msg.err.range.start.lt.zero"));
        if(length < 0) throw new IllegalArgumentException(msgs.getString("msg.err.range.len.lt.zero"));

        this.start  = start;
        this.length = length;
        this.end    = (start + length);
    }

    public boolean isInRange(int value) {
        return ((value >= start) && (value < end));
    }

    public static int closedRangeCount(int start, int end, int step) {
        int count = rangeCount(start, end, step);
        int xxx   = (start + (count * step));
        return ((end == xxx) ? (count + 1) : count);
    }

    public static boolean isInClosedRange(int v, int a, int b) {
        return ((v >= Math.min(a, b)) && (v <= Math.max(a, b)));
    }

    public static boolean isInRange(int v, int a, int b) {
        return ((v >= Math.min(a, b)) && (v < Math.max(a, b)));
    }

    public static int rangeCount(int start, int end, int step) {
        if(start <= end) {
            if(step <= 0) throw new IllegalArgumentException(msgs.format("msg.err.range.count.bad_step", msgs.getString("text.greater"), step, "<="));
            return (int)Math.ceil((((double)end) - ((double)start)) / ((double)step));
        }
        if(step >= 0) throw new IllegalArgumentException(msgs.format("msg.err.range.count.bad_step", msgs.getString("text.less"), step, ">="));
        return (int)Math.ceil((((double)start) - ((double)end)) / ((double)Math.abs(step)));
    }

    @Contract("_, _ -> new")
    public static @NotNull Range valueOf(int start, int end) {
        if(start < 0) throw new IllegalArgumentException(msgs.getString("msg.err.range.start.lt.zero"));
        if(end < 0) throw new IllegalArgumentException(msgs.getString("msg.err.range.end.lt.zero"));
        if(end < start) throw new IllegalArgumentException(msgs.getString("msg.err.range.end.lt.start"));

        return new Range(start, (end - start));
    }

    @Contract("_ -> new")
    public static @NotNull Range valueOf(Matcher matcher) {
        return Range.valueOf(matcher, 0);
    }

    @Contract("_, _ -> new")
    public static @NotNull Range valueOf(@NotNull Matcher matcher, int group) {
        int s = matcher.start(group);
        int e = matcher.end(group);
        return new Range(s, (e - s));
    }
}
