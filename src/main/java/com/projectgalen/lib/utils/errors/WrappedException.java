package com.projectgalen.lib.utils.errors;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: WrappedException.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: November 28, 2023
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

@SuppressWarnings("unused")
public class WrappedException extends RuntimeException {
    public WrappedException()                                                                   { }

    public WrappedException(@NotNull String message)                                            { super(message); }

    public WrappedException(@NotNull String message, @NotNull Throwable cause)                  { super(message, cause); }

    public WrappedException(@NotNull Throwable cause)                                           { super(cause); }

    public static void exec(@NotNull ThrowingRunnable runner)                                   { exec(runner, () -> { }); }

    public static void exec(@NotNull ThrowingRunnable runner, @NotNull Runnable thenFinally)    { try { runner.run(); } catch(Throwable t) { throw wrap(t); } finally { thenFinally.run(); } }

    public static <R> R get(@NotNull ThrowingSupplier<R> runner)                                { return get(runner, () -> { }); }

    public static <R> R get(@NotNull ThrowingSupplier<R> runner, @NotNull Runnable thenFinally) { try { return runner.get(); } catch(Throwable t) { throw wrap(t); } finally { thenFinally.run(); } }

    public static @NotNull WrappedException wrap(@NotNull Throwable t)                          { return ((t instanceof WrappedException) ? ((WrappedException)t) : new WrappedException(t)); }
}
