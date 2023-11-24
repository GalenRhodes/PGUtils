package com.projectgalen.lib.utils.errors;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: SQLRuntimeException.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: November 22, 2023
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

import com.projectgalen.lib.utils.delegates.ThrowingRunnable;
import com.projectgalen.lib.utils.delegates.ThrowingSupplier;
import org.jetbrains.annotations.NotNull;

public final class SQLRuntimeException extends RuntimeException {
    public SQLRuntimeException()                                          { super(); }

    public SQLRuntimeException(String message)                            { super(message); }

    public SQLRuntimeException(Throwable cause)                           { super(cause); }

    public SQLRuntimeException(String message, Throwable cause)           { super(message, cause); }

    public static @NotNull SQLRuntimeException cast(@NotNull Throwable t) { return ((t instanceof SQLRuntimeException) ? ((SQLRuntimeException)t) : new SQLRuntimeException(t)); }

    public static void exec(@NotNull ThrowingRunnable runnable) throws SQLRuntimeException {
        try {
            runnable.run();
        }
        catch(Throwable t) {
            throw cast(t);
        }
    }

    public static <T> T get(@NotNull ThrowingSupplier<T> supplier) throws SQLRuntimeException {
        try {
            return supplier.get();
        }
        catch(Throwable t) {
            throw cast(t);
        }
    }
}
