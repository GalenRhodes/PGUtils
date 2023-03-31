package com.projectgalen.lib.utils.concurrency;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Trigger.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: March 27, 2023
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

import com.projectgalen.lib.utils.delegates.VoidDelegate;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Trigger {
    private final ReentrantLock            lock            = new ReentrantLock(true);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final VoidDelegate             delegate;
    private final int                      _delay;
    private final TimeUnit                 _unit;
    private       boolean                  triggered       = false;

    public Trigger(int delay, @NotNull TimeUnit unit, @NotNull VoidDelegate delegate) {
        this.delegate = delegate;
        this._delay   = delay;
        this._unit    = unit;
    }

    public void trigger() {
        Locks.doWithLock(lock, () -> setTrigger(_delay, _unit));
    }

    public void trigger(int delay, TimeUnit unit) {
        Locks.doWithLock(lock, () -> setTrigger(delay, unit));
    }

    private void doTriggerAction() {
        if(triggered) try { delegate.action(); } finally { triggered = false; }
    }

    private void setTrigger(int delay, TimeUnit unit) {
        if(!triggered) {
            triggered = true;
            executorService.schedule(() -> Locks.doWithLock(lock, this::doTriggerAction), delay, unit);
        }
    }
}
