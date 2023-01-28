package com.projectgalen.lib.utils.macro;
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
import com.projectgalen.lib.utils.delegates.MacroDelegate;
import com.projectgalen.lib.utils.errors.InvalidPropertyMacro;
import com.projectgalen.lib.utils.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

public final class Macro {
    private static final                     PGProperties     props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);
    private static final                     PGResourceBundle msgs;
    private static final @Language("RegExp") String           rx0;
    private static final                     String           rx1;
    private static final                     String           rx2;
    private static final                     String           rx3;

    static {
        msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
        rx0  = props.getProperty("macro.regexp", false);
        rx1  = props.getProperty("macro.double_slash", false);
        rx2  = props.getProperty("macro.double_slash.repl", false);
        rx3  = props.getProperty("macro.single_slash", false);
    }

    private Macro() { }

    public static String replaceMacros(String input, @NotNull MacroDelegate delegate) {
        return ((input == null) ? null : replaceMacros(input, new TreeSet<>(), delegate));
    }

    private static String getMacroReplacement(@NotNull String macroName, @NotNull Set<String> deadManSet, @NotNull MacroDelegate delegate) {
        if(deadManSet.contains(macroName)) throw new InvalidPropertyMacro(msgs.format("msg.err.macro.key_circular_ref", macroName));
        deadManSet.add(macroName);
        String input = delegate.getMacroValue(macroName);
        return ((input == null) ? null : replaceMacros(input, deadManSet, delegate));
    }

    @NotNull
    private static String replaceMacros(String input, @NotNull Set<String> deadManSet, @NotNull MacroDelegate delegate) {
        return Regex.replaceUsingDelegate(rx0, input.replace(rx1, rx2), m -> getMacroReplacement(m.group(1), deadManSet, delegate)).replace(rx3, "").replace(rx2, rx3);
    }
}
