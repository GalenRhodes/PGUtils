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

import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.errors.InvalidPropertyMacro;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public final class Macro {
    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private Macro() { }

    public static @Contract("null,_->null;!null,_->!null") String replaceMacros(String input, @NotNull Function<String, String> macroFunc) {
        return ofNullable(input).map(s -> processMacros(s, new TreeSet<>(), macroFunc)).orElse(null);
    }

    private static @NotNull String processMacros(@NotNull String input, Set<String> deadManSet, Function<String, String> macroFunc) {
        StringBuilder sb   = new StringBuilder();
        int           len  = input.length();
        int           pos  = 0;
        int           last = 0;

        while(pos < len) {
            switch(input.charAt(pos++)) {
                case '\\' -> { if(pos < len) last = pos = prom1(sb, input, last, pos); }
                case '$' -> last = pos = prom2(sb.append(input, last, pos - 1), input, pos, len, deadManSet, macroFunc);
            }
        }

        return sb.append(input, last, len).toString();
    }

    private static int prom1(@NotNull StringBuilder sb, String input, int last, int pos) {
        sb.append(input, last, pos - 1).append(input.charAt(pos));
        return (pos + 1);
    }

    private static int prom2(StringBuilder sb, @NotNull String input, int pos, int inputLen, Set<String> deadManSet, Function<String, String> macroFunc) {
        return ((input.charAt(pos++) == '{') ? prom3(sb, input, pos, inputLen, deadManSet, macroFunc) : prom5(sb, input, pos));
    }

    private static int prom3(StringBuilder sb, @NotNull String input, int pos, int inputLen, Set<String> deadManSet, Function<String, String> macroFunc) {
        int start = pos;
        while(pos < inputLen) if(input.charAt(pos++) == '}') return prom4(sb, input, start, pos, deadManSet, macroFunc);
        return prom5(sb, input, inputLen);
    }

    private static @Contract("_, _, _, _, _, _ -> param4") int prom4(StringBuilder sb, @NotNull String input, int start, int pos, Set<String> deadManSet, Function<String, String> macroFunc) {
        String name = input.substring(start, pos - 1).trim();
        if(name.isEmpty()) sb.append("${}");
        else if(deadManSet.contains(name)) throw new InvalidPropertyMacro(msgs.format("msg.err.macro.key_circular_ref", name));
        else ofNullable(macroFunc.apply(name)).ifPresentOrElse(r -> prom6(sb, name, r, deadManSet, macroFunc), () -> sb.append(input, start - 2, pos));
        return pos;
    }

    private static @Contract("_, _, _ -> param3") int prom5(@NotNull StringBuilder sb, @NotNull String input, int pos) {
        sb.append(input, pos - 2, pos);
        return pos;
    }

    private static void prom6(@NotNull StringBuilder sb, String name, String rep, @NotNull Set<String> deadManSet, Function<String, String> macroFunc) {
        deadManSet.add(name);
        sb.append(processMacros(rep, deadManSet, macroFunc));
        deadManSet.remove(name);
    }
}
