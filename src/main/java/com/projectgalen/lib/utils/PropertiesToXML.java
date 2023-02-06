package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PropertiesToXML.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: February 01, 2023
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

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SameParameterValue")
public final class PropertiesToXML {
    private static final PGResourceBundle    msgs          = PGResourceBundle.getPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties        xmlProps      = PGProperties.getXMLProperties("pg_props2xml.xml", PropertiesToXML.class);
    private static final String              FMT           = "%s%s\n";
    private static final String              KEYFMT        = "%s.%d";
    private static final String              DBL_DASH      = "--";
    private static final String              DBL_DASH_REPL = ("" + (char)xmlProps.getInt("xml.comment.dash.replacement", 8211)).repeat(2);
    private static final String              TAB           = " ".repeat(Math.max(0, xmlProps.getInt("xml.indent.width", 4)));
    private static final Map<String, String> XMLCHARS      = xmlProps.getMap("xml.special.repl", "\\s*,\\s*", "\\s*:\\s*");

    public PropertiesToXML() { }

    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println(msgs.getString("msg.err.p2x.no.props.file.specified"));
            System.exit(1);
        }

        for(String filename : args) {
            File fileIn  = new File(filename);
            File fileOut = new File(fileIn.getParentFile(), String.format("%s.xml", U.getPart(fileIn.getName(), "\\.", U.Parts.NOT_LAST)));

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), StandardCharsets.ISO_8859_1))) {
                String line = reader.readLine();

                if(line != null) {
                    try(PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut), StandardCharsets.UTF_8)))) {
                        Pattern patt = Pattern.compile(xmlProps.getProperty("xml.comment.regexp"));
                        writeMultipleLines(writer, "xml.header", "");

                        do {
                            Matcher m = patt.matcher(line);

                            if(m.matches()) {
                                writeComment(writer, m.group(1));
                            }
                            else {
                                int idx = line.indexOf('=');
                                if(idx > 0) { writeValue(writer, line, idx); }
                                else { writeComment(writer, line.trim()); }
                            }

                            line = reader.readLine();
                        }
                        while(line != null);

                        writeMultipleLines(writer, "xml.footer", "");
                        writer.flush();
                    }
                }
            }
            catch(Exception e) {
                System.err.printf(msgs.getString("msg.err.p2x.error"), e);
                System.exit(1);
            }
        }

        System.exit(0);
    }

    private static void writeMultipleLines(@NotNull PrintWriter writer, @NotNull String pfx, @NotNull String tab) {
        for(int i = 1; i < 100; i++) {
            String s = xmlProps.getProperty(String.format(KEYFMT, pfx, i));
            if(s == null) return;
            writer.printf(FMT, tab, s);
        }
    }

    private static void writeValue(@NotNull PrintWriter writer, @NotNull String line, int idx) {
        String key = escapeSpecialChars(line.substring(0, idx++).trim(), true);
        String val = ((idx < line.length()) ? escapeSpecialChars(line.substring(idx), false) : "");
        writer.printf(FMT, TAB, xmlProps.format((val.length() == 0) ? "xml.entry.novalue" : "xml.entry.value", key, val));
    }

    private static @NotNull String escapeSpecialChars(@NotNull String str, boolean doQuotes) {
        StringBuilder sb       = new StringBuilder();
        String        xmlQuote = xmlProps.getProperty("xml.quote");

        for(int i = 0, j = str.length(); i < j; i++) {
            char   ch = str.charAt(i);
            String cs = ("" + ch);
            sb.append((doQuotes && ch == '"') ? xmlQuote : Objects.toString(XMLCHARS.get(cs), cs));
        }

        return sb.toString();
    }

    private static void writeComment(@NotNull PrintWriter writer, @NotNull String str) {
        writer.printf(FMT, TAB, xmlProps.format("xml.comment.format", str.replace(DBL_DASH, DBL_DASH_REPL)));
    }
}
