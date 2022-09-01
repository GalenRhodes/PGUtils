package com.projectgalen.lib.utils.concurrency;

import com.projectgalen.lib.utils.delegates.ReturningDelegate;
import com.projectgalen.lib.utils.delegates.ReturningThrowingDelegate;
import com.projectgalen.lib.utils.delegates.VoidDelegate;
import com.projectgalen.lib.utils.delegates.VoidThrowingDelegate;

import java.util.concurrent.locks.Lock;

public class Locks {
    private Locks() {
    }

    public static void doWithLock(Lock lock, VoidDelegate delegate) {
        lock.lock();
        try { delegate.action(); }
        finally { lock.unlock(); }
    }

    public static void doWithLockThrows(Lock lock, VoidThrowingDelegate delegate) throws Exception {
        lock.lock();
        try { delegate.action(); }
        finally { lock.unlock(); }
    }

    public static <T> T getWithLock(Lock lock, ReturningDelegate<T> delegate) {
        lock.lock();
        try { return delegate.action(); }
        finally { lock.unlock(); }
    }

    public static <T> T getWithLockThrows(Lock lock, ReturningThrowingDelegate<T> delegate) throws Exception {
        lock.lock();
        try { return delegate.action(); }
        finally { lock.unlock(); }
    }
}
