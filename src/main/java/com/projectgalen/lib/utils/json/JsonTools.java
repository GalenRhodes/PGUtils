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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TimeZone;

@SuppressWarnings("unused")
public final class JsonTools {
    private JsonTools() { }

    public static @NotNull ObjectMapper getObjectMapper() {
        return new ObjectMapper().setTimeZone(TimeZone.getDefault()).enable(JsonParser.Feature.IGNORE_UNDEFINED, JsonParser.Feature.AUTO_CLOSE_SOURCE).enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> void writeJsonFile(@NotNull File file, T value) throws IOException {
        try(OutputStream outputStream = new FileOutputStream(file)) {
            ObjectMapper mapper = getObjectMapper();
            mapper.enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
            mapper.writeValue(outputStream, value);
            outputStream.flush();
        }
    }

    public static <T> void writeJsonFile(@NotNull String pathname, T value) throws IOException {
        writeJsonFile(new File(pathname), value);
    }

    public static <T> void writeJsonFile(@NotNull String dir, @NotNull String filename, T value) throws IOException {
        writeJsonFile(new File(dir, filename), value);
    }

    public static <T> T readJsonFile(@NotNull String dir, @NotNull String filename, @NotNull Class<T> rootClass) throws IOException {
        return readJsonFile(dir, filename, rootClass, false);
    }

    public static <T> T readJsonFile(@NotNull String filePath, @NotNull Class<T> rootClass) throws IOException {
        return readJsonFile(new File(filePath), rootClass, false);
    }

    public static <T> T readJsonFile(@NotNull String dir, @NotNull String filename, @NotNull Class<T> rootClass, boolean acceptSingleValueAsArray) throws IOException {
        return readJsonFile(new File(dir, filename), rootClass, acceptSingleValueAsArray);
    }

    public static <T> T readJsonFile(@NotNull String filePath, @NotNull Class<T> rootClass, boolean acceptSingleValueAsArray) throws IOException {
        return readJsonFile(new File(filePath), rootClass, acceptSingleValueAsArray);
    }

    public static <T> T readJsonFile(@NotNull File file, @NotNull Class<T> rootClass) throws IOException {
        return readJsonFile(file, rootClass, false);
    }

    public static <T> T readJsonFile(@NotNull File file, @NotNull Class<T> rootClass, boolean acceptSingleValueAsArray) throws IOException {
        ObjectMapper mapper = getObjectMapper();
        if(acceptSingleValueAsArray) mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return mapper.readValue(file, rootClass);
    }
}
