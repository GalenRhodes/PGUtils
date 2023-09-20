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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({ "unchecked", "unused" })
public class EventListeners {

    private final List<Pair> listeners = new ArrayList<>();

    public EventListeners() { }

    public <L extends EventListener> void add(@NotNull Class<L> cls, @NotNull L listener) {
        synchronized(listeners) {
            listeners.removeIf(p -> (p.getListener() == null));
            if(stream(cls).noneMatch(l -> (l.equals(listener)))) listeners.add(new Pair(cls, listener));
        }
    }

    public <L extends EventListener, E extends EventObject> void fireEvent(@NotNull Class<L> cls, @NotNull E event, @NotNull BiConsumer<L, E> biConsumer) {
        synchronized(listeners) { stream(cls).forEach(l -> biConsumer.accept(l, event)); }
    }

    public <L extends EventListener> void forEach(@NotNull Class<L> cls, @NotNull Consumer<L> consumer) {
        synchronized(listeners) { stream(cls).forEach(consumer); }
    }

    public <L extends EventListener> @NotNull List<L> getListeners(@NotNull Class<L> cls) {
        synchronized(listeners) { return stream(cls).collect(Collectors.toList()); }
    }

    public <L extends EventListener> void remove(@NotNull Class<L> cls, @NotNull L listener) {
        synchronized(listeners) { listeners.removeIf(p -> _pred(cls, p.getCls(), listener, p.getListener())); }
    }

    private boolean _pred(@NotNull Class<?> c1, @NotNull Class<?> c2, @NotNull EventListener l1, @Nullable EventListener l2) {
        return ((l2 == null) || ((c1 == c2) && l1.equals(l2)));
    }

    private <L extends EventListener> @NotNull Stream<L> stream(@NotNull Class<L> cls) {
        return listeners.stream().filter(p -> (p.getCls() == cls)).map(p -> (L)p.getListener()).filter(Objects::nonNull);
    }

    @SuppressWarnings("NullableProblems")
    private static class Pair {
        private final @NotNull Class<? extends EventListener> cls;
        private final @NotNull EventListener                  listener;
        // private final @NotNull WeakReference<EventListener>   listener;

        public Pair(@NotNull Class<? extends EventListener> cls, @NotNull EventListener listener) {
            this.cls = cls;
            // this.listener = new WeakReference<>(listener);
            this.listener = listener;
        }

        public @NotNull Class<? extends EventListener> getCls() {
            return cls;
        }

        public EventListener getListener() {
            // return listener.get();
            return listener;
        }
    }
}
