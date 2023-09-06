package com.projectgalen.lib.utils.collections.ring;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: ByteRingBuffer.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: August 25, 2023
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ByteRingBuffer extends AbstractRingBuffer<Byte> {

    public ByteRingBuffer(int initSize)                                                 { super(initSize); }

    public ByteRingBuffer()                                                             { super(); }

    public int get()                                                                    { return get1().map(this::bar).orElse(-2); }

    public int get(byte @NotNull [] buf, int off, int len)                              { return get1(buf, off, len); }

    public int get(byte @NotNull [] buf)                                                { return get1(buf, 0, buf.length); }

    public void put(int aByte)                                                          { put1((byte)(aByte & 0x00ff)); }

    public void put(byte @NotNull [] buf, int off, int len)                             { put1(buf, off, len); }

    public void put(byte @NotNull [] buf)                                               { put1(buf, 0, buf.length); }

    public int wget(byte @NotNull [] buf, int off, int len) throws InterruptedException { return waitFor(() -> get2(buf, off, len)).orElse(-1); }

    public int wget(byte @NotNull [] buf) throws InterruptedException                   { return waitFor(() -> get2(buf, 0, buf.length)).orElse(-1); }

    public int wget() throws InterruptedException                                       { return waitFor(() -> bar(get2())).orElse(-1); }

    protected @Override @NotNull Object createArray(int size)                           { return new byte[size]; }

    private int bar(@NotNull Optional<Byte> ob)                                         { return ob.map(this::map).orElse(-1); }

    private int map(byte b)                                                             { return (b & 0x00ff); }

    private final class RingInputStream extends InputStream {
        private boolean strmClsd = false;

        public RingInputStream()                                                           { super(); }

        public int available()                                                             { return ByteRingBuffer.this.available(); }

        public @Override void close()                                                      { doLocked(() -> strmClsd = true); }

        public @Override int read() throws IOException                                     { return foo(() -> bar(get2())).orElse(-1); }

        public @Override int read(byte @NotNull [] b, int off, int len) throws IOException { return foo(() -> get2(b, off, len)).orElse(-1); }

        public @Override long skip(long n)                                                 { return ByteRingBuffer.this.skip(n); }

        private <T> Optional<T> foo(@NotNull Supplier<T> s) throws IOException             { try { return waitFor(() -> strmClsd, s); } catch(Exception e) { throw new InterruptedIOException(); } }
    }

    private final class RingOutputStream extends OutputStream {

        private boolean strmClsd = false;

        public RingOutputStream()                                                            { super(); }

        @Override public void close()                                                        { doLocked(() -> strmClsd = true); }

        @Override public void write(byte @NotNull [] b, int off, int len) throws IOException { foo(() -> put2(b, off, len)); }

        public @Override void write(int b) throws IOException                                { foo(() -> put2((byte)(b & 0x00ff))); }

        private void foo(@NotNull Runnable runnable) throws IOException {
            if(getLocked(() -> {
                if(closed || strmClsd) return true;
                runnable.run();
                return false;
            })) throw new IOException("Output stream is closed.");
        }
    }
}
