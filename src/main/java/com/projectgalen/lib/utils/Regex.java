package com.projectgalen.lib.utils;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Regex {
    private Regex() {
    }

    public static @NotNull String replaceUsingDelegate(@NotNull @Language("RegExp") @RegExp @NonNls String pattern,
                                                       @NotNull CharSequence input,
                                                       @NotNull Regex.ReplacementDelegate delegate) {
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

    public interface ReplacementDelegate {
        String getReplacement(Matcher matcher);
    }
}
