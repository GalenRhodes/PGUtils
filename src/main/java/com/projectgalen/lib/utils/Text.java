package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Text.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: March 22, 2023
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

import com.projectgalen.lib.utils.enums.Align;
import com.projectgalen.lib.utils.refs.BooleanRef;
import com.projectgalen.lib.utils.refs.ObjectRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class Text {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private Text() { }

    public static <T> @Nullable T forEachCodePoint(@NotNull String str, int startIndex, @Nullable T defaultReturnValue, @NotNull CodePointIteratorHandler<T> handler) {
        return forEachCodePoint(str, startIndex, str.length(), defaultReturnValue, handler);
    }

    public static <T> @Nullable T forEachCodePoint(@NotNull String str, int startIndex, @NotNull CodePointIteratorHandler<T> handler) {
        return forEachCodePoint(str, startIndex, str.length(), null, handler);
    }

    public static <T> @Nullable T forEachCodePoint(@NotNull String str, @Nullable T defaultReturnValue, @NotNull CodePointIteratorHandler<T> handler) {
        return forEachCodePoint(str, 0, str.length(), defaultReturnValue, handler);
    }

    public static <T> @Nullable T forEachCodePoint(@NotNull String str, @NotNull CodePointIteratorHandler<T> handler) {
        return forEachCodePoint(str, 0, str.length(), null, handler);
    }

    public static <T> @Nullable T forEachCodePoint(@NotNull String str, int startIndex, int endIndex, @Nullable T defaultReturnValue, @NotNull CodePointIteratorHandler<T> handler) {
        if(startIndex > endIndex) throw new IllegalArgumentException(msgs.format("msg.err.text.start_greater_than_end", startIndex, endIndex));
        if(startIndex == endIndex) return null;
        if(startIndex < 0) throw new IllegalArgumentException(msgs.format("msg.err.text.start_neg", startIndex));
        if(endIndex > str.length()) throw new IllegalArgumentException(msgs.format("msg.err.text.end_greater_than_length", endIndex, str.length()));

        ObjectRef<T> retValue = new ObjectRef<>(defaultReturnValue);
        BooleanRef   stop     = new BooleanRef(false);
        int          idx      = startIndex;

        while((idx < endIndex) && !stop.value) {
            char ch = str.charAt(idx++);
            if(Character.isHighSurrogate(ch) && (idx < endIndex)) {
                char ch2 = str.charAt(idx);
                if(Character.isLowSurrogate(ch2)) {
                    idx++;
                    handler.action(Character.toCodePoint(ch, ch2), stop, retValue);
                    continue;
                }
            }
            handler.action(ch, stop, retValue);
        }

        return retValue.value;
    }

    public static boolean isAllWhitespace(@NotNull String str, int startIndex, int endIndex) {
        return Null.ifNull(forEachCodePoint(str, startIndex, endIndex, true, (cp, stop, returnValueRef) -> {
            if(!Character.isWhitespace(cp)) {
                //noinspection ConstantValue
                returnValueRef.value = !(stop.value = true);
            }
        }), false);
    }

    public static boolean isAllWhitespace(@NotNull String str) {
        return isAllWhitespace(str, 0, str.length());
    }

    public static boolean isWS(@NotNull String str, int idx) {
        char ch = str.charAt(idx);
        return ((Character.isHighSurrogate(ch) && ((idx + 1) < str.length())) ? Character.isWhitespace(str.codePointAt(idx)) : Character.isWhitespace(ch));
    }

    public static @NotNull String pad(String str, int width) {
        return pad(str, Align.Left, width, true);
    }

    public static @NotNull String pad(String str, Align align, int width, boolean trim) {
        if(str == null) return String.valueOf(PGArrays.createAndFill(width, ' '));

        str = str.strip();
        int strLen = str.length();

        if(strLen <= width) return _pad(str, align, width);
        if(!trim) return str;

        switch(align) {/*@f0*/
            case Left:  return str.substring(0, width);
            case Right: return str.substring(strLen - width);
            default:    return str.substring(((strLen - width) / 2), (((strLen - width) / 2) + width));
        }/*@f1*/
    }

    public static @NotNull String pad(String str, Align align, int width) {
        return pad(str, align, width, true);
    }

    public static String @NotNull [] padWithWrap(String str, Align align, int width, boolean stripEachLineLeading) {
        if(str == null) return new String[] { String.valueOf(PGArrays.createAndFill(width, ' ')) };

        String[]     lines = str.split("\\r\\n|\\n");
        List<String> out   = new ArrayList<>();

        for(String _line : lines) {
            String line = (stripEachLineLeading ? _line.strip() : _line.stripTrailing());

            if(line.length() == width) {
                out.add(line);
            }
            else if(line.length() < width) {
                out.add(_pad(line, align, width));
            }
            else {
                for(String l : _wrap(line, width)) out.add(_pad(l, align, width));
            }
        }

        return out.toArray(new String[0]);
    }

    public static @NotNull List<String> wrap(@NotNull String str, int width, boolean stripLeading) {
        return _wrap((stripLeading ? str.strip() : str.stripTrailing()), width);
    }

    private static @NotNull String _pad(@NotNull String str, @NotNull Align align, int width) {
        int strLen = str.length();

        if(strLen == width) return str;

        char[] buffer = PGArrays.createAndFill(width, ' ');

        switch(align) {/*@f0*/
            case Left:  System.arraycopy(str.toCharArray(), 0, buffer, 0, strLen); break;
            case Right: System.arraycopy(str.toCharArray(), 0, buffer, (width - strLen), strLen); break;
            default:    System.arraycopy(str.toCharArray(), 0, buffer, ((width - strLen) / 2), strLen); break;
        }/*@f1*/

        return String.valueOf(buffer);
    }

    private static @NotNull List<String> _wrap(String str, int width) {
        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(str);

        int          brk   = 0;
        int          start = 0;
        int          end   = str.length();
        int          idx   = iterator.next();
        List<String> list  = new ArrayList<>();

        while(idx != BreakIterator.DONE) {
            boolean fits = ((idx - start) <= width);

            if(idx == end) {
                if(fits) {
                    list.add(str.substring(start));
                }
                else if(brk == start) {
                    int j = _wrap01(list, str, start, idx, width);
                    list.add(str.substring(j));
                }
                else {
                    int i = _wrap02(list, iterator, str, start, brk);
                    int j = _wrap01(list, str, i, idx, width);
                    list.add(str.substring(j));
                }
                return list;
            }

            if(isWS(str, idx)) {
                if(fits) {
                    brk = idx;
                }
                else if(brk == start) {
                    start = _wrap01(list, str, start, idx, width);
                    brk   = idx;
                }
                else {
                    brk = start = _wrap02(list, iterator, str, start, brk);
                }
            }

            idx = iterator.next();
        }

        return list;
    }

    private static int _wrap01(@NotNull List<String> list, @NotNull String str, int start, int idx, int limit) {
        while((idx - start) > limit) start = _wrap03(list, str, start, (start + limit));
        return start;
    }

    private static int _wrap02(@NotNull List<String> list, @NotNull BreakIterator iterator, @NotNull String str, int start, int end) {
        return iterator.following(_wrap03(list, str, start, end));
    }

    private static int _wrap03(@NotNull List<String> list, @NotNull String str, int start, int end) {
        list.add(str.substring(start, end));
        return end;
    }

    public interface CodePointIteratorHandler<T> {
        void action(int codePoint, BooleanRef stopRef, ObjectRef<T> returnValueRef);
    }
}
