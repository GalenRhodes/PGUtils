package com.projectgalen.lib.utils.text;

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

import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.collections.PGArrays;
import com.projectgalen.lib.utils.enums.Align;
import com.projectgalen.lib.utils.enums.Parts;
import com.projectgalen.lib.utils.text.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.BreakIterator;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.projectgalen.lib.utils.text.regex.Regex.getUnicodeMatcher;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public final class Text {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private static final String sss = "␀␁␂␃␄␅␆␇␈␉␊␌␌␍␎␏␐␑␒␓␔␕␖␗␘␙␚␛␜␝␞␟␡";

    private Text() { }

    public static @NotNull StringBuilder appendFormat(@NotNull StringBuilder sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
    }

    public static @NotNull StringBuffer appendFormat(@NotNull StringBuffer sb, @NotNull String format, @Nullable Object... args) {
        return sb.append(String.format(format, args));
    }

    public static byte @NotNull [] base64Decode(@NotNull String encStr) {
        return Base64.getDecoder().decode(encStr);
    }

    public static @NotNull String base64Encode(byte @NotNull [] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static @NotNull String capitalize(@NotNull String str) {
        return ((str.isEmpty()) ? str : ((str.length() == 1) ? str.toUpperCase() : (str.substring(0, 1).toUpperCase() + str.substring(1))));
    }

    public static @NotNull String cleanNumberString(String numberString) {
        return toNonEmptyString(Objects.toString(numberString, "0").replaceAll("[^0-9.+-]", ""), "0");
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static int @NotNull [] codePointAt(char @NotNull [] chars, int idx, boolean backwards) {
        if(!backwards) return codePointAt(chars, idx);
        if(idx < 1 || idx > chars.length) return new int[] { -1, idx };
        char c2 = chars[--idx];
        if(Character.isLowSurrogate(c2) && (idx > 0)) {
            char c1 = chars[idx - 1];
            if(Character.isHighSurrogate(c1)) return new int[] { Character.toCodePoint(c1, c2), (idx - 1) };
        }
        return new int[] { c2, idx };
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static int @NotNull [] codePointAt(char @NotNull [] chars, int idx) {
        if(idx < 0 || idx >= chars.length) return new int[] { -1, idx };
        char c1 = chars[idx++];
        if(Character.isHighSurrogate(c1) && (idx < chars.length)) {
            char c2 = chars[idx];
            if(Character.isLowSurrogate(c2)) return new int[] { Character.toCodePoint(c1, c2), (idx + 1) };
        }
        return new int[] { c1, idx };
    }

    @Contract("_, _ -> param1")
    public static @NotNull StringBuffer concat(@NotNull StringBuffer sb, Object @NotNull ... args) {
        for(Object o : args) sb.append(o);
        return sb;
    }

    @Contract("_, _ -> param1")
    public static @NotNull StringBuilder concat(@NotNull StringBuilder sb, Object @NotNull ... args) {
        for(Object o : args) sb.append(o);
        return sb;
    }

    public static @NotNull String concat(Object... args) {
        return concat(new StringBuilder(), args).toString();
    }

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

    public static @NotNull String getPart(@NotNull String str, @NotNull @NonNls @Language("RegExp") String separator, @NotNull Parts part) {
        Matcher m = Regex.getMatcher(separator, str);
        return switch(part) {/*@f0*/
            case NOT_FIRST -> (m.find() ? str.substring(m.end()) : str);
            case NOT_LAST  -> (m.find() ? str.substring(0, getLastMatchLocation(m, Matcher::start)) : str);
            case LAST      -> (m.find() ? str.substring(getLastMatchLocation(m, Matcher::end)) : str);
            default        -> (m.find() ? str.substring(0, m.start()) : str);
        };/*@f1*/
    }

    public static @NotNull String ifNullOrEmpty(@Nullable String str, @NotNull String def) {
        return ((str == null || str.isEmpty()) ? def : str);
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

    public static @NotNull String join(char separator, Object @NotNull ... args) {
        return join(Character.toString(separator), args);
    }

    public static @NotNull String join(@NotNull String separator, Object @NotNull ... args) {
        if(args.length == 0) return "";
        StringBuilder sb = new StringBuilder().append(args[0]);
        for(int i = 1; i < args.length; i++) sb.append(separator).append(args[i]);
        return sb.toString();
    }

    public static @NotNull String join(String @NotNull [] array, int start, int end, @NotNull String joiner) {
        if((start < 0) || (end < 0) || (end > array.length) || (start > end)) throw new IllegalArgumentException();
        if(start == end) return "";
        String first = array[start++];
        if(start == end) return first;
        StringBuilder sb = new StringBuilder().append(first);
        while(start < end) sb.append(joiner).append(array[start++]);
        return sb.toString();
    }

    @Contract("!null -> !null; null -> null")
    public static String lc(@Nullable String str) {
        return ((str == null) ? null : str.toLowerCase());
    }

    @Contract(pure = true)
    public static @Nullable String nullIfEmpty(@Nullable String str) {
        return (z(str) ? null : str);
    }

    public static boolean nz(@Nullable String str) {
        return ((str != null) && (!str.trim().isEmpty()));
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

    public static @NotNull String @NotNull [] splitDotPath(@NotNull String path) {
        int i = path.lastIndexOf('.');
        return ((i >= 0) ? new String[] { path.substring(0, i), path.substring(i + 1) } : new String[] { path });
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

    public static @NotNull String toNonEmptyString(@Nullable String str, @NotNull String defaultString) {
        return toNonEmptyString(str, () -> defaultString);
    }

    public static @NotNull String toNonEmptyString(@Nullable String str, @NotNull Supplier<String> defaultSupplier) {
        String s = ((str == null) ? "" : str.trim());
        return (s.isEmpty() ? defaultSupplier.get() : s);
    }

    public static String toString(@Nullable String str, @NotNull Supplier<String> supplier) {
        return Optional.ofNullable(str).orElseGet(supplier);
    }

    @Contract("!null -> !null; null -> null")
    public static String tr(@Nullable String str) {
        return ((str == null) ? null : str.trim());
    }

    @Contract("!null -> !null; null -> null")
    public static String uc(@Nullable String str) {
        return ((str == null) ? null : str.toUpperCase());
    }

    public static @NotNull List<String> wrap(@NotNull String str, int width, boolean stripLeading) {
        return _wrap((stripLeading ? str.strip() : str.stripTrailing()), width);
    }

    public static boolean z(@Nullable String str) {
        return ((str == null) || (str.trim().isEmpty()));
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

    private static int getLastMatchLocation(Matcher m, @NotNull Function<Matcher, Integer> function) {
        int i = function.apply(m);
        while(m.find()) i = function.apply(m);
        return i;
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
