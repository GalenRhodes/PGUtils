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
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.projectgalen.lib.utils.regex.Regex.getUnicodeMatcher;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public final class Text {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private Text() { }

    /**
     * Converts a string from kebab-case or SCREAMING-KEBAB-CASE to camelCase.
     *
     * @param charSequence The text to convert.
     *
     * @return The camelCase version of the string.
     */
    public static @NotNull String convertKebabCaseToCamelCase(@NotNull CharSequence charSequence) { return convertKebabCaseToCamelCase(charSequence, false); }

    /**
     * Converts a string from kebab-case or SCREAMING-KEBAB-CASE to camelCase.
     *
     * @param charSequence           The text to convert.
     * @param keepLeadingAndTrailing If set to true then any leading and/or trailing dashes will be kept.
     *
     * @return The camelCase version of the string.
     */
    public static @NotNull String convertKebabCaseToCamelCase(@NotNull CharSequence charSequence, boolean keepLeadingAndTrailing) {
        return convertToCamelCase(charSequence, keepLeadingAndTrailing, "-+", '-');
    }

    /**
     * Converts a string from kebab-case or SCREAMING-KEBAB-CASE to PascalCase.
     *
     * @param charSequence The text to convert.
     *
     * @return The camelCase version of the string.
     */
    public static String convertKebabCaseToPascalCase(@NotNull CharSequence charSequence) { return convertKebabCaseToPascalCase(charSequence, false); }

    /**
     * Converts a string from kebab-case or SCREAMING-KEBAB-CASE to PascalCase.
     *
     * @param charSequence           The text to convert.
     * @param keepLeadingAndTrailing If set to true then any leading and/or trailing dashes will be kept.
     *
     * @return The camelCase version of the string.
     */
    public static String convertKebabCaseToPascalCase(@NotNull CharSequence charSequence, boolean keepLeadingAndTrailing) {
        return getUnicodeMatcher("^-*[^-]", convertKebabCaseToCamelCase(charSequence, keepLeadingAndTrailing)).replaceAll(m -> m.group().toUpperCase());
    }

    /**
     * Converts a string from snake_case or SCREAMING_SNAKE_CASE to camelCase.
     *
     * @param charSequence The text to convert.
     *
     * @return The camelCase version of the string.
     */
    public static @NotNull String convertSnakeCaseToCamelCase(@NotNull CharSequence charSequence) { return convertSnakeCaseToCamelCase(charSequence, false); }

    /**
     * Converts a string from snake_case or SCREAMING_SNAKE_CASE to camelCase.
     *
     * @param charSequence           The text to convert.
     * @param keepLeadingAndTrailing If set to true then any leading and/or trailing underscores will be kept.
     *
     * @return The camelCase version of the string.
     */
    public static @NotNull String convertSnakeCaseToCamelCase(@NotNull CharSequence charSequence, boolean keepLeadingAndTrailing) {
        return convertToCamelCase(charSequence, keepLeadingAndTrailing, "_+", '_');
    }

    /**
     * Converts a string from snake_case or SCREAMING_SNAKE_CASE to PascalCase.
     *
     * @param charSequence The text to convert.
     *
     * @return The camelCase version of the string.
     */
    public static String convertSnakeCaseToPascalCase(@NotNull CharSequence charSequence) {
        return getUnicodeMatcher("^_*[^_]", convertSnakeCaseToCamelCase(charSequence)).replaceAll(m -> m.group().toUpperCase());
    }

    public static boolean isAllWhitespace(@NotNull CharSequence charSequence, int startIndex, int endIndex) {
        return streamCodePoints(charSequence, startIndex, endIndex).allMatch(cp -> Character.isWhitespace(cp.codePoint));
    }

    public static boolean isAllWhitespace(@NotNull CharSequence charSequence) {
        return isAllWhitespace(charSequence, 0, charSequence.length());
    }

    public static boolean isWS(@NotNull CharSequence charSequence, int idx) {
        char ch1 = charSequence.charAt(idx++);
        if(Character.isHighSurrogate(ch1) && (idx < charSequence.length())) {
            char ch2 = charSequence.charAt(idx);
            if(Character.isLowSurrogate(ch2)) return Character.isWhitespace(Character.toCodePoint(ch1, ch2));
        }
        return Character.isWhitespace(ch1);
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

        return switch(align) {/*@f0*/
            case Left  -> str.substring(0, width);
            case Right -> str.substring(strLen - width);
            default    -> str.substring(((strLen - width) / 2), (((strLen - width) / 2) + width));
        };/*@f1*/
    }

    public static @NotNull String pad(String str, Align align, int width) {
        return pad(str, align, width, true);
    }

    public static String @NotNull [] padWithWrap(String str, Align align, int width, boolean stripEachLineLeading) {
        if(str == null) return new String[] { String.valueOf(PGArrays.createAndFill(width, ' ')) };

        String[] lines = str.split("\\R");
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

    public static boolean startsWithIgnoreCase(@NotNull String string, @NotNull String subString) {
        int l = subString.length();
        if(l > string.length()) return false;
        for(int i = 0; i < l; i++) if(Character.toLowerCase(subString.charAt(i)) != Character.toLowerCase(string.charAt(i))) return false;
        return true;
    }

    public static @NotNull Stream<CodePointPosition> streamCodePoints(@NotNull CharSequence charSequence) {
        return streamCodePoints(charSequence, 0, charSequence.length());
    }

    public static @NotNull Stream<CodePointPosition> streamCodePoints(@NotNull CharSequence charSequence, int startIndex) {
        return streamCodePoints(charSequence, startIndex, charSequence.length());
    }

    public static @NotNull Stream<CodePointPosition> streamCodePoints(@NotNull CharSequence charSequence, int startIndex, int endIndex) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new MyCodePointIterator(charSequence, startIndex, endIndex), Spliterator.IMMUTABLE | Spliterator.ORDERED), false);
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

    private static boolean contains(@NotNull CharSequence charSequence, int index, char @NotNull ... characters) {
        char ch1 = charSequence.charAt(index);
        for(char ch2 : characters) if(ch1 == ch2) return true;
        return false;
    }

    private static @NotNull String convertToCamelCase(@NotNull CharSequence charSequence, boolean keepLeadingAndTrailing, @Language("RegExp") String regex, char... x) {
        Matcher m = getUnicodeMatcher(regex, charSequence);
        if(m.find()) {
            StringBuilder sb = new StringBuilder();
            int           i  = 0;
            do {
                if(m.start() == 0) {
                    if(keepLeadingAndTrailing) sb.append(charSequence, 0, m.end());
                }
                else {
                    sb.append(charSequence.subSequence(i, m.start()).toString().toLowerCase());
                    i = m.end();
                    if(i < charSequence.length()) {
                        char ch1 = charSequence.charAt(i++);
                        if(Character.isHighSurrogate(ch1) && i < charSequence.length()) {
                            char ch2 = charSequence.charAt(i);
                            if(Character.isLowSurrogate(ch2)) {
                                i++;
                                int    cp  = Character.toUpperCase(Character.toCodePoint(ch1, ch2));
                                char[] out = Character.toChars(cp);
                            }
                        }
                    }
                }
            }
            while(m.find());

            return sb.toString();
        }
        return charSequence.toString().toLowerCase();
    }

    private static @NotNull List<String> getMessage(int startIndex, int endIndex, int len) {
        List<String> arr = new ArrayList<>();
        if(startIndex < 0) arr.add(msgs.format("msg.err.text.code_point_iterator.start_index_less_than_zero", startIndex));
        if(startIndex > len) arr.add(msgs.format("msg.err.text.code_point_iterator.start_index_greater_than_input_length", startIndex, len));
        if(endIndex < 0) arr.add(msgs.format("msg.err.text.code_point_iterator.end_index_less_than_zero", endIndex));
        if(endIndex > len) arr.add(msgs.format("msg.err.text.code_point_iterator.end_index_greater_than_input_length", endIndex, len));
        if(startIndex > endIndex) arr.add(msgs.format("msg.err.text.code_point_iterator.start_index_greater_than_end_index", startIndex, endIndex));
        return arr;
    }

    public record CodePointPosition(int codePoint, int index) implements Comparable<CodePointPosition> {
        public @Override int compareTo(@NotNull Text.CodePointPosition o) {
            return Integer.compare(codePoint, o.codePoint);
        }
    }

    private static final class MyCodePointIterator implements Iterator<CodePointPosition> {
        private final CharSequence charSequence;
        private final int          endIndex;
        private       int          currentIndex;

        public MyCodePointIterator(@NotNull CharSequence charSequence, int startIndex, int endIndex) {
            this.charSequence = charSequence;
            int len = charSequence.length();

            if((startIndex >= 0) && (startIndex <= endIndex) && (endIndex <= len)) {
                this.endIndex = endIndex;
                currentIndex  = startIndex;
                if((currentIndex > 0) && Character.isLowSurrogate(charSequence.charAt(currentIndex)) && Character.isHighSurrogate(charSequence.charAt(currentIndex - 1))) currentIndex -= 1;
            }
            else {
                throw new IllegalArgumentException(getMessage(startIndex, endIndex, len).stream().collect(Collectors.joining("; ", "", "")));
            }
        }

        public @Override boolean hasNext() {
            return (currentIndex < endIndex);
        }

        public @Override @NotNull CodePointPosition next() {
            if(currentIndex >= endIndex) throw new NoSuchElementException();
            int  idx = currentIndex;
            char ch1 = charSequence.charAt(currentIndex++);

            if(Character.isHighSurrogate(ch1) && (currentIndex < charSequence.length())) {
                char ch2 = charSequence.charAt(currentIndex + 1);

                if(Character.isLowSurrogate(ch2)) {
                    currentIndex++;
                    return new CodePointPosition(Character.toCodePoint(ch1, ch2), idx);
                }
            }

            return new CodePointPosition(ch1, idx);
        }
    }
}
