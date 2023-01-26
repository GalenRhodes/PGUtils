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
import com.projectgalen.lib.utils.delegates.MacroDelegate;
import com.projectgalen.lib.utils.errors.InvalidPropertyMacro;
import com.projectgalen.lib.utils.regex.Regex;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

public class Macro {
    private static final                     Properties     _props;
    private static final                     ResourceBundle _msgs;
    private static final @Language("RegExp") String         _rx0;
    private static final                     String         _rx1;
    private static final                     String         _rx2;
    private static final                     String         PG_PROPERTIES = "pg_properties.properties";

    static {
        _msgs = ResourceBundle.getBundle("com.projectgalen.lib.utils.pg_messages");
        try(InputStream inputStream = PGProperties.class.getResourceAsStream(PG_PROPERTIES)) {
            _props = new Properties();
            _props.load(inputStream);
            _rx0 = _props.getProperty("macro.regexp");
            _rx1 = _props.getProperty("macro.double_slash.regexp");
            _rx2 = _props.getProperty("macro.double_slash.replacement");
        }
        catch(IOException e) {
            throw new RuntimeException(String.format(_msgs.getString("msg.err.missing_resource_file"), PG_PROPERTIES));
        }
    }

    private Macro() {
    }

    public static @NotNull String replaceMacros(@NotNull String input, @NotNull MacroDelegate delegate) {
        return replaceMacros(input, new TreeSet<>(), delegate);
    }

    private static String getMacroReplacement(@NotNull String macroName, @NotNull Set<String> deadManSet, @NotNull MacroDelegate delegate) {
        if(deadManSet.contains(macroName)) throw new InvalidPropertyMacro(String.format(_msgs.getString("msg.err.macro_key_circular_ref"), macroName));
        deadManSet.add(macroName);
        String oval = delegate.getMacroValue(macroName);
        return ((oval == null) ? null : replaceMacros(oval, deadManSet, delegate));
    }

    private static @NotNull String replaceMacros(@NotNull String input, @NotNull Set<String> deadManSet, @NotNull MacroDelegate delegate) {
        return Regex.replaceUsingDelegate(_rx0, input.replaceAll(_rx1, _rx2), (m) -> getMacroReplacement(m.group(1), deadManSet, delegate)).replaceAll(_rx2, _rx1);
    }
}
