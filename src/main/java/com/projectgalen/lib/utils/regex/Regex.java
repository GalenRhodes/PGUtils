package com.projectgalen.lib.utils.regex;
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

import com.projectgalen.lib.utils.ObjCache;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.MagicConstant;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Regex {
    private Regex() {
    }

    public static @NotNull Matcher getMatcher(@NotNull @NonNls @Language("RegExp") String pattern, @NotNull @NonNls CharSequence input) {
        return getMatcher(pattern, 0, input);
    }

    public static @NotNull Matcher getMatcher(@NotNull @NonNls @Language("RegExp") String pattern,
                                              @MagicConstant(flagsFromClass = Pattern.class) int flags,
                                              @NotNull @NonNls CharSequence input) {
        String  key = String.format("♚%s♛%d", pattern, flags);
        Pattern p   = CacheHolder.CACHE.get(key, Pattern.class);
        if(p == null) {
            p = Pattern.compile(pattern, flags);
            CacheHolder.CACHE.store(key, p);
        }
        return p.matcher(input);
    }

    public static @NotNull String replaceUsingDelegate(@NotNull @Language("RegExp") @RegExp @NonNls String pattern,
                                                       @NotNull CharSequence input,
                                                       @NotNull ReplacementDelegate delegate) {
        Pattern p = Pattern.compile(pattern);
        return replaceUsingDelegate(p, input, delegate);
    }

    public static @NotNull String replaceUsingDelegate(@NotNull Pattern pattern, @NotNull CharSequence input, @NotNull ReplacementDelegate delegate) {
        Matcher m = pattern.matcher(input);

        if(!m.find()) return input.toString();

        StringBuilder sb = new StringBuilder();

        do {
            String repl = delegate.getReplacement(m);
            m.appendReplacement(sb, Matcher.quoteReplacement(repl == null ? m.group() : repl));
        }
        while(m.find());

        m.appendTail(sb);
        return sb.toString();
    }

    private static final class CacheHolder {
        private static final ObjCache CACHE = new ObjCache();
    }
}
