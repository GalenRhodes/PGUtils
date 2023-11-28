package com.projectgalen.lib.utils.io;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: ReaderInputStream.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: November 12, 2023
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

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

public class ReaderInputStream extends FilterInputStream {

    private final @SuppressWarnings("FieldCanBeLocal") Thread                     thread;
    private final                                      AtomicReference<Exception> threadError = new AtomicReference<>(null);

    public ReaderInputStream(@NotNull Reader reader, @NotNull Charset cs) throws IOException {
        this(reader, false, cs);
    }

    public ReaderInputStream(@NotNull Reader reader, boolean closeReaderWhenDone, @NotNull Charset cs) {
        super(new PipedInputStream(1_048_576));
        this.thread = new Thread(() -> runner(reader, cs, closeReaderWhenDone));
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public @Override int read(byte @NotNull [] b, int off, int len) throws IOException {
        int cc = super.read(b, off, len);
        if((cc == -1) && (threadError.get() instanceof IOException e)) throw e;
        return cc;
    }

    public @Override int read() throws IOException {
        int b = super.read();
        if((b == -1) && (threadError.get() instanceof IOException e)) throw e;
        return b;
    }

    private void runner(@NotNull Reader reader, @NotNull Charset cs, boolean closeReader) {
        try(Writer writer = new OutputStreamWriter(new PipedOutputStream((PipedInputStream)in), cs)) { IO.copy(reader, writer, false, false); }
        catch(IOException e) { threadError.set(e); }
        finally { if(closeReader) IO.closeQuietly(reader); }
    }
}
