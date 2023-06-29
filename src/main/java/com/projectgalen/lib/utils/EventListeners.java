package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: EventListeners.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: June 29, 2023
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class EventListeners {

    private final List<Pair> listeners = new ArrayList<>();

    public EventListeners() { }

    public <T extends EventListener> void add(@NotNull Class<T> cls, @NotNull T listener) {
        synchronized(listeners) {
            listeners.removeIf(Pair::isEmpty);
            if(stream(cls).noneMatch(l -> (l == listener))) listeners.add(new Pair(cls, listener));
        }
    }

    public <T extends EventListener> void forEach(@NotNull Class<T> cls, @NotNull Consumer<T> consumer) {
        synchronized(listeners) { stream(cls).forEach(consumer); }
    }

    public <T extends EventListener> @NotNull List<T> getListeners(@NotNull Class<T> cls) {
        synchronized(listeners) { return stream(cls).collect(Collectors.toList()); }
    }

    public <T extends EventListener> void remove(@NotNull Class<T> cls, @NotNull T listener) {
        synchronized(listeners) { listeners.removeIf(p -> (p.isEmpty() || ((p.cls == cls) && (p.listener.get() == listener)))); }
    }

    protected <T extends EventListener> @NotNull Stream<T> stream(@NotNull Class<T> cls) {
        return listeners.stream().filter(p -> (p.cls == cls)).map(p -> (T)p.listener.get()).filter(Objects::nonNull);
    }

    private static class Pair {
        public final @NotNull Class<? extends EventListener> cls;
        public final @NotNull WeakReference<EventListener>   listener;

        public Pair(@NotNull Class<? extends EventListener> cls, @NotNull EventListener listener) {
            this.cls      = cls;
            this.listener = new WeakReference<>(listener);
        }

        public boolean isEmpty() { return listener.get() == null; }
    }
}
