package com.projectgalen.lib.utils.json;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: JsonTools.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 18, 2023
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

@SuppressWarnings("unused")
public final class JsonTools {
    private JsonTools() { }

    public static @NotNull ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.enable(JsonParser.Feature.IGNORE_UNDEFINED, JsonParser.Feature.AUTO_CLOSE_SOURCE);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    public static <T> T readJsonFile(String dir, String filename, Class<T> rootClass) throws IOException {
        return readJsonFile(new File(dir, filename), rootClass);
    }

    public static <T> T readJsonFile(File file, Class<T> rootClass) throws IOException {
        return getObjectMapper().readValue(file, rootClass);
    }
}
