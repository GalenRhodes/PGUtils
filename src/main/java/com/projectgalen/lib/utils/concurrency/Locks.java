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

import java.util.concurrent.locks.Lock;

public final class Locks {
    private Locks() { }

    public static void doWithLock(Lock lock, VoidDelegate delegate) {
        lock.lock();
        try { delegate.action(); }
        finally { lock.unlock(); }
    }

    public static void doWithLockThrows(Lock lock, VoidThrowsDelegate delegate) throws Exception {
        lock.lock();
        try { delegate.action(); }
        finally { lock.unlock(); }
    }

    public static <T> T getWithLock(Lock lock, GetDelegate<T> delegate) {
        lock.lock();
        try { return delegate.action(); }
        finally { lock.unlock(); }
    }

    public static <T> T getWithLockThrows(Lock lock, GetThrowsDelegate<T> delegate) throws Exception {
        lock.lock();
        try { return delegate.action(); }
        finally { lock.unlock(); }
    }
}
