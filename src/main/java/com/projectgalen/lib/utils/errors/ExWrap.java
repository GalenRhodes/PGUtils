package com.projectgalen.lib.utils.errors;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: ExWrap.java
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

import java.util.concurrent.Future;

@SuppressWarnings("unused")
public class ExWrap extends RuntimeException {
    public ExWrap()                                                                   { }

    public ExWrap(@NotNull String message)                                            { super(message); }

    public ExWrap(@NotNull String message, @NotNull Throwable cause)                  { super(message, cause); }

    public ExWrap(@NotNull Throwable cause)                                           { super(cause); }

    public static void exec(@NotNull ThrowingRunnable runner)                                   { exec(runner, () -> { }); }

    public static void exec(@NotNull ThrowingRunnable runner, @NotNull Runnable thenFinally)    { try { runner.run(); } catch(Throwable t) { throw wrap(t); } finally { thenFinally.run(); } }

    public static <R> R get(@NotNull ThrowingSupplier<R> runner)                                { return get(runner, () -> { }); }

    public static <R> R get(@NotNull ThrowingSupplier<R> runner, @NotNull Runnable thenFinally) { try { return runner.get(); } catch(Throwable t) { throw wrap(t); } finally { thenFinally.run(); } }

    public static <R> R get(@NotNull Future<R> future)                                { return get(future, () -> { }); }

    public static <R> R get(@NotNull Future<R> future, @NotNull Runnable thenFinally) { return get(future::get, thenFinally); }

    public static @NotNull ExWrap wrap(@NotNull Throwable t)                          { return ((t instanceof ExWrap) ? ((ExWrap)t) : new ExWrap(t)); }
}
