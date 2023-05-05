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

import com.projectgalen.lib.utils.delegates.GetDelegate;
import com.projectgalen.lib.utils.delegates.GetThrowsDelegate;
import com.projectgalen.lib.utils.delegates.VoidDelegate;
import com.projectgalen.lib.utils.delegates.VoidThrowsDelegate;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;

public final class Locks {
    private Locks() { }

    public static void doWithLock(@NotNull Lock lock, @NotNull VoidDelegate delegate) {
        lock.lock();
        try { delegate.action(); } finally { lock.unlock(); }
    }

    public static <E extends Throwable> void doWithLockThrows(@NotNull Lock lock, @NotNull VoidThrowsDelegate<E> delegate) throws E {
        lock.lock();
        try { delegate.action(); } finally { lock.unlock(); }
    }

    public static <T> T getWithLock(@NotNull Lock lock, @NotNull GetDelegate<T> delegate) {
        lock.lock();
        try { return delegate.action(); } finally { lock.unlock(); }
    }

    public static <T, E extends Throwable> T getWithLockThrows(@NotNull Lock lock, @NotNull GetThrowsDelegate<T, E> delegate) throws E {
        lock.lock();
        try { return delegate.action(); } finally { lock.unlock(); }
    }
}
