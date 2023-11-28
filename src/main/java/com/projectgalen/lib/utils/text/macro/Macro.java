package com.projectgalen.lib.utils.text.macro;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Macro.java
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
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.errors.InvalidPropertyMacro;
import com.projectgalen.lib.utils.text.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;

public final class Macro {
    private static final                     PGProperties     props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);
    private static final                     PGResourceBundle msgs;
    private static final @Language("RegExp") String           rx1;
    private static final                     String           rx2;
    private static final                     String           rx3;
    private static final                     String           rx4;
    private static final @Language("RegExp") String           rx5;

    private Macro() { }

    public static String replaceMacros(String input, @NotNull Function<String, String> stringFunction) {
        return ((input == null) ? null : replaceMacros(input, new TreeSet<>(), stringFunction));
    }

    private static String getMacroReplacement(@NotNull String macroName, @NotNull Set<String> deadManSet, @NotNull Function<String, String> stringFunction) {
        if(deadManSet.contains(macroName)) throw new InvalidPropertyMacro(msgs.format("msg.err.macro.key_circular_ref", macroName));
        try {
            deadManSet.add(macroName);
            String input = stringFunction.apply(macroName);
            return ((input == null) ? null : replaceMacros(input, deadManSet, stringFunction));
        }
        finally {
            deadManSet.remove(macroName);
        }
    }

    private static @NotNull String replaceMacros(String input, @NotNull Set<String> deadManSet, @NotNull Function<String, String> stringFunction) {
        String        str = Regex.replaceUsingDelegate(rx1, input.replace(rx2, rx3), m -> getMacroReplacement(m.group(1), deadManSet, stringFunction));
        StringBuilder sb  = new StringBuilder();
        Matcher       m   = Regex.getMatcher(rx5, str);
        while(m.find()) m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)));
        return m.appendTail(sb).toString().replace(rx3, rx4);
    }

    static {
        msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
        rx1  = props.getProperty("macro.regexp", false);
        rx2  = props.getProperty("macro.double_slash", false);
        rx3  = props.getProperty("macro.double_slash.repl", false);
        rx4  = props.getProperty("macro.single_slash", false);
        rx5  = props.getProperty("macro.regexp.quiet", false);
    }
}
