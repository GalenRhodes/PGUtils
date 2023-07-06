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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class Trigger {
    private final ScheduledExecutorService executorService;
    private final Runnable                 delegate;
    private final int                      delay;
    private final TimeUnit                 unit;
    private       boolean                  triggered;

    public Trigger(int delay, @NotNull TimeUnit unit, @NotNull Runnable delegate) {
        this.delegate        = delegate;
        this.delay           = delay;
        this.unit            = unit;
        this.triggered       = false;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void cancel() {
        cancel(null);
    }

    public void cancel(@Nullable Runnable delegateOverride) {
        synchronized(unit) { runDelegate(delegateOverride); }
    }

    public void trigger(int delayOverride, TimeUnit unitOverride) {
        synchronized(unit) { setTrigger(delayOverride, unitOverride); }
    }

    public void trigger() {
        synchronized(unit) { setTrigger(delay, unit); }
    }

    private void doTriggerAction() {
        synchronized(unit) { if(triggered) runDelegate(delegate); }
    }

    private void runDelegate(@Nullable Runnable _delegate) {
        try { if(_delegate != null) _delegate.run(); }
        finally { triggered = false; }
    }

    private void setTrigger(int _delay, TimeUnit _unit) {
        if(!triggered) {
            triggered = true;
            executorService.schedule(this::doTriggerAction, _delay, _unit);
        }
    }
}
