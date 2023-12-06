package com.projectgalen.lib.utils.text.regex;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Regex.java
//         IDE: IntelliJ
//      AUTHOR: Galen Rhodes
//        DATE: January 05, 2023
//
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

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.collections.ObjCache;
import com.projectgalen.lib.utils.math.Range;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.regex.Pattern.UNICODE_CASE;
import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;

@SuppressWarnings("unused")
public final class Regex {

    private static final PGProperties props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);

    private Regex() { }

    public static @NotNull Matcher getMatcher(@NotNull @NonNls @Language("RegExp") String pattern, @NotNull @NonNls CharSequence input) {
        return getMatcher(pattern, 0, input);
    }

    public static @NotNull Matcher getMatcher(@NotNull @NonNls @Language("RegExp") String pattern, @MagicConstant(flagsFromClass = Pattern.class) int flags, @NotNull @NonNls CharSequence input) {
        synchronized(CacheHolder.CACHE) {
            String  key = String.format(props.getProperty("regex.cache.key.format", false), pattern, flags);
            Pattern p   = CacheHolder.CACHE.get(key, Pattern.class);
            if(p == null) {
                try {
                    p = Pattern.compile(pattern, flags);
                    CacheHolder.CACHE.store(key, p);
                }
                catch(Throwable e) {
                    e.printStackTrace(System.err);
                    throw new RuntimeException(e);
                }
            }
            return p.matcher(input);
        }
    }

    public static @NotNull Matcher getUnicodeMatcher(@NotNull @NonNls @Language("RegExp") String pattern,
                                                     @MagicConstant(flagsFromClass = Pattern.class) int flags,
                                                     @NotNull @NonNls CharSequence input) {
        return getMatcher(pattern, UNICODE_CHARACTER_CLASS | UNICODE_CASE | flags, input);
    }

    public static @NotNull Matcher getUnicodeMatcher(@NotNull @NonNls @Language("RegExp") String pattern, @NotNull @NonNls CharSequence input) {
        return getMatcher(pattern, UNICODE_CHARACTER_CLASS | UNICODE_CASE, input);
    }

    public static Range rangeOfFirstMatch(@NotNull @NonNls @Language("RegExp") String pattern, @MagicConstant(flagsFromClass = Pattern.class) int flags, @NotNull @NonNls CharSequence input) {
        return rangeOfLastMatch(getMatcher(pattern, flags, input));
    }

    public static Range rangeOfFirstMatch(@NotNull @NonNls @Language("RegExp") String pattern, @NotNull @NonNls CharSequence input) {
        return rangeOfLastMatch(getMatcher(pattern, input));
    }

    public static Range rangeOfFirstMatch(@NotNull Matcher matcher) {
        return (matcher.find() ? Range.valueOf(matcher) : null);
    }

    public static Range rangeOfLastMatch(@NotNull @NonNls @Language("RegExp") String pattern, @MagicConstant(flagsFromClass = Pattern.class) int flags, @NotNull @NonNls CharSequence input) {
        return rangeOfLastMatch(getMatcher(pattern, flags, input));
    }

    public static Range rangeOfLastMatch(@NotNull @NonNls @Language("RegExp") String pattern, @NotNull @NonNls CharSequence input) {
        return rangeOfLastMatch(getMatcher(pattern, input));
    }

    public static Range rangeOfLastMatch(@NotNull Matcher matcher) {
        Range r = rangeOfFirstMatch(matcher);
        if(r != null) { while(matcher.find()) r = Range.valueOf(matcher); }
        return r;
    }

    public static @NotNull Stream<MatchPoint> streamMatches(@NotNull Pattern pattern, @NotNull CharSequence input) {
        return streamMatches(pattern.matcher(input), input);
    }

    public static @NotNull Stream<MatchPoint> streamMatches(@NotNull @NonNls @Language("RegExp") String pattern, @NotNull CharSequence input) {
        return streamMatches(pattern, 0, input);
    }

    public static @NotNull Stream<MatchPoint> streamMatches(@NotNull @NonNls @Language("RegExp") String pattern,
                                                            @MagicConstant(flagsFromClass = Pattern.class) int flags,
                                                            @NotNull CharSequence input) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new MatchIterator(pattern, flags, input), Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED), false);
    }

    public static @NotNull Stream<MatchPoint> streamMatches(@NotNull Matcher matcher, @NotNull CharSequence input) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new MatchIterator(matcher, input), Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED), false);
    }

    private static final class CacheHolder {
        private static final ObjCache CACHE = new ObjCache();
    }
}
