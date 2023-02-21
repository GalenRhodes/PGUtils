package com.projectgalen.lib.utils;

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

import java.util.regex.Matcher;

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

    public static Range valueOf(int start, int end) {
        if(start < 0) throw new IllegalArgumentException(msgs.getString("msg.err.range.start.lt.zero"));
        if(end < 0) throw new IllegalArgumentException(msgs.getString("msg.err.range.end.lt.zero"));
        if(end < start) throw new IllegalArgumentException(msgs.getString("msg.err.range.end.lt.start"));

        return new Range(start, (end - start));
    }

    public static Range valueOf(Matcher matcher) {
        return Range.valueOf(matcher, 0);
    }

    public static Range valueOf(Matcher matcher, int group) {
        int s = matcher.start(group);
        int e = matcher.end(group);
        return new Range(s, (e - s));
    }
}
