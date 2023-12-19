package com.projectgalen.lib.utils.text;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: CharArraySequence.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: December 19, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import com.projectgalen.lib.utils.PGResourceBundle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CharArraySequence implements CharSequence {

    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private final char @NotNull [] buffer;
    private final int              start;
    private final int              length;

    private CharArraySequence(char @NotNull [] buffer, int start, int length) {
        this.buffer = buffer;
        this.start  = start;
        this.length = length;
    }

    public @Override char charAt(int index) {
        if((start + index) >= buffer.length) throw new IndexOutOfBoundsException(getErrorMessage("msg.name.index", index, length, ">="));
        return buffer[start + index];
    }

    public @Override int length() {
        return length;
    }

    public @NotNull @Override CharSequence subSequence(int start, int end) {
        validateStartIndex(start, length);
        if(end < start) throw new IllegalArgumentException(getErrorMessage("msg.name.end_index", end, start, "<"));
        if(end > length) throw new IllegalArgumentException(getErrorMessage("msg.name.end_index", end, length, ">"));

        return new CharArraySequence(buffer, (this.start + start), (end - start));
    }

    public @Override @NotNull String toString() {
        return String.copyValueOf(buffer, start, length);
    }

    @Contract(value = "_ -> new", pure = true) public static @NotNull CharArraySequence noCopyValueOf(char @NotNull [] buffer) {
        return new CharArraySequence(buffer, 0, buffer.length);
    }

    @Contract("_, _, _ -> new") public static @NotNull CharArraySequence noCopyValueOf(char @NotNull [] buffer, int start, int length) {
        validateParameters(start, length, buffer.length);
        return new CharArraySequence(buffer, start, length);
    }

    @Contract(value = "_ -> new", pure = true) public static @NotNull CharArraySequence valueOf(char @NotNull [] buffer) {
        return new CharArraySequence(Arrays.copyOf(buffer, buffer.length), 0, buffer.length);
    }

    @Contract("_, _, _ -> new") public static @NotNull CharArraySequence valueOf(char @NotNull [] buffer, int start, int length) {
        validateParameters(start, length, buffer.length);
        return new CharArraySequence(Arrays.copyOfRange(buffer, start, (start + length)), 0, length);
    }

    private static @NotNull String getErrorMessage(@NotNull String key, int val1, int val2, String op) {
        return "%s %s: %d %s %d".formatted(msgs.getString(key), msgs.getString("msg.err.out_of_bounds"), val1, op, val2);
    }

    private static void validateParameters(int start, int length, int bufferLength) {
        validateStartIndex(start, bufferLength);
        if(length < 0) throw new IllegalArgumentException(getErrorMessage("msg.name.length", length, 0, "<"));
        if((start + length) > bufferLength) throw new IllegalArgumentException(getErrorMessage("msg.name.length", length, (bufferLength - start), ">"));
    }

    private static void validateStartIndex(int start, int bufferLength) {
        if(start < 0) throw new IllegalArgumentException(getErrorMessage("msg.name.start_index", start, 0, "<"));
        if(start > bufferLength) throw new IllegalArgumentException(getErrorMessage("msg.name.start_index", start, bufferLength, ">"));
    }
}
