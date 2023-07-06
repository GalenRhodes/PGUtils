package com.projectgalen.lib.utils.concurrency;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Locks.java
//         IDE: IntelliJ
//      AUTHOR: Galen Rhodes
//        DATE: January 05, 2023
//
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

import com.projectgalen.lib.utils.delegates.ThrowingRunnable;
import com.projectgalen.lib.utils.delegates.ThrowingSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public final class Locks {
    private Locks() { }

    public static void doWithLock(@NotNull Lock lock, @NotNull Runnable delegate) {
        lock.lock();
        try { delegate.run(); } finally { lock.unlock(); }
    }

    public static <E extends Throwable> void doWithLockThrows(@NotNull Lock lock, @NotNull ThrowingRunnable<E> delegate) throws E {
        lock.lock();
        try { delegate.run(); } finally { lock.unlock(); }
    }

    public static <T> T getWithLock(@NotNull Lock lock, @NotNull Supplier<T> delegate) {
        lock.lock();
        try { return delegate.get(); } finally { lock.unlock(); }
    }

    public static <T, E extends Throwable> T getWithLockThrows(@NotNull Lock lock, @NotNull ThrowingSupplier<T, E> delegate) throws E {
        lock.lock();
        try { return delegate.get(); } finally { lock.unlock(); }
    }
}
