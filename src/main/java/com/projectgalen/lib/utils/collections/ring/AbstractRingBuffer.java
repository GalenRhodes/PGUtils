package com.projectgalen.lib.utils.collections.ring;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: AbstractRingBuffer.java
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

import com.projectgalen.lib.utils.PGResourceBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Array;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings({ "SuspiciousSystemArraycopy", "unchecked" })
public abstract class AbstractRingBuffer<E> implements AutoCloseable {

    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    protected final int     initSize;
    protected final String  lock   = UUID.randomUUID().toString();
    protected       int     head   = 0;
    protected       int     tail   = 0;
    protected       boolean closed = false;
    protected       int     capacity;
    protected       Object  buffer;

    public AbstractRingBuffer(int initSize) {
        this.initSize = Math.max(initSize, 1024 * 1024);
        this.capacity = this.initSize;
        this.buffer   = createArray(this.initSize);
    }

    public AbstractRingBuffer() {
        this(1024 * 1024);
    }

    public int available() {
        return getLocked(false, this::count);
    }

    public int capacity() {
        return getLocked(false, () -> capacity);
    }

    public void close() {
        doLocked(() -> {
            closed = true;
            head   = tail = 0;
            buffer = null;
        });
    }

    public boolean isClosed() {
        return getLocked(false, () -> closed);
    }

    public boolean isEmpty() {
        return getLocked(false, () -> (head == tail));
    }

    public int skip(long n) {
        return getLocked(() -> {
            int len = (int)Math.max(Math.min(n, (closed ? 0 : count())), 0);
            incHead(len);
            return len;
        });
    }

    public <T> Optional<T> waitFor(@NotNull Supplier<Boolean> stopOn, @NotNull Supplier<T> supplier) throws InterruptedException {
        synchronized(lock) { return wait1(stopOn) ? Optional.empty() : Optional.ofNullable(supplier.get()); }
    }

    public <T> Optional<T> waitFor(@NotNull Supplier<T> supplier) throws InterruptedException {
        return waitFor(() -> false, supplier);
    }

    protected abstract @NotNull Object createArray(@Range(from = 0, to = Integer.MAX_VALUE) int size);

    protected void doLocked(@NotNull Runnable runnable) {
        synchronized(lock) { try { runnable.run(); } finally { lock.notify(); } }
    }

    protected @NotNull Optional<Optional<E>> get1() {
        return getLocked(() -> Optional.ofNullable(closed ? null : get2()));
    }

    protected int get1(@NotNull Object buf, int off, int len) {
        return getLocked(() -> get2(buf, off, len));
    }

    protected @NotNull Optional<E> get2() {
        return ((head == tail) ? Optional.empty() : get3());
    }

    protected int get2(@NotNull Object buf, int off, int len) {
        return (closed ? -1 : cpOut(buf, off, len));
    }

    protected <T> T getLocked(@NotNull Supplier<T> supplier) {
        return getLocked(true, supplier);
    }

    protected <T> T getLocked(boolean notify, @NotNull Supplier<T> supplier) {
        synchronized(lock) { try { return supplier.get(); } finally { if(notify) lock.notify(); } }
    }

    protected void put1(@NotNull Object buf, int off, int len) {
        if(getLocked(() -> put2(buf, off, len))) throw new IllegalStateException(msgs.getString("msg.err.ringbuff.buffer_is_closed"));
    }

    protected void put1(@NotNull E value) {
        if(getLocked(() -> put2(value))) throw new IllegalStateException(msgs.getString("msg.err.ringbuff.buffer_is_closed"));
    }

    protected boolean put2(@NotNull Object buf, int off, int len) {
        if(closed) return true;
        ensure(len);
        cpIn(buf, off, len);
        return false;
    }

    protected boolean put2(@NotNull E value) {
        if(closed) return true;
        ensure(1);
        Array.set(buffer, incTail(1), value);
        return false;
    }

    private int count() {
        return ((head <= tail) ? (tail - head) : ((capacity - head) + tail));
    }

    private void cpIn(@NotNull Object buf, int off, int len) {
        if(tail <= head) {
            if(tail == head) head = tail = 0;
            System.arraycopy(buf, off, buffer, incTail(len), len);
        }
        else {
            int l1 = Math.min(len, (capacity - tail));
            System.arraycopy(buf, off, buffer, incTail(len), l1);
            System.arraycopy(buf, (off + l1), buffer, 0, (len - l1));
        }
    }

    private int cpOut(@NotNull Object buf, int off, int len) {
        if((head == tail) || (len <= 0)) return 0;
        int l1 = Math.min(len, count());
        if(head < tail) {
            System.arraycopy(buffer, incHead(l1), buf, off, l1);
        }
        else {
            int l2 = Math.min(l1, (capacity - head));
            System.arraycopy(buffer, incHead(l1), buf, off, l2);
            System.arraycopy(buffer, 0, buf, (off + l2), (l1 - l2));
        }
        return l1;
    }

    private void ensure(int needed) {
        if(needed > 0) {
            int cap = capacity;
            int cnt = count();

            if((cap - cnt - 1) < needed) {
                do cap = incSize(cap); while((cap - cnt - 1) < needed);
                Object buf = createArray(cap);
                cpOut(buf, 0, cnt);
                head     = 0;
                tail     = cnt;
                buffer   = buf;
                capacity = cap;
            }
        }
    }

    private @NotNull Optional<E> get3() {
        return Optional.of((E)Array.get(buffer, incHead(1)));
    }

    private int incHead(@Range(from = 0, to = Integer.MAX_VALUE) int delta) {
        int orgHead = head;
        head = ((head + delta) % capacity);
        return orgHead;
    }

    private int incSize(int sz) {
        long l = (long)Math.floor(sz * 1.75);
        if(l <= Integer.MAX_VALUE) return (int)l;
        throw new IllegalStateException(msgs.getString("msg.err.ringbuff.buffer_too_large"));
    }

    private int incTail(@Range(from = 0, to = Integer.MAX_VALUE) int delta) {
        int orgTail = tail;
        tail = ((tail + delta) % capacity);
        return orgTail;
    }

    private boolean wait1(@NotNull Supplier<Boolean> stopOn) throws InterruptedException {
        do {
            if(head != tail) return false;
            if(closed || stopOn.get()) return true;
            lock.wait();
        }
        while(true);
    }
}
