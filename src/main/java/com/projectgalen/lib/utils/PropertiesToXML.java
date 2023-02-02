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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class PropertiesToXML {
    private static final PGResourceBundle msgs = PGResourceBundle.getPGBundle("com.projectgalen.lib.utils.pg_messages");

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
                Properties p = new Properties();
                p.load(reader);

                try(BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileOut))) {
                    p.storeToXML(outputStream, null, StandardCharsets.UTF_8);
                    outputStream.flush();
                }
            }
            catch(Exception e) {
                System.err.printf(msgs.getString("msg.err.p2x.error"), e);
                System.exit(1);
            }
        }

        System.exit(0);
    }
}
