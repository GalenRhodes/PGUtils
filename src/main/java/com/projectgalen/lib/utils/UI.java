package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: UI.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 02, 2023
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

import com.intellij.uiDesigner.core.GridConstraints;
import com.projectgalen.lib.utils.refs.ObjectRef;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

public final class UI {

    public static final int CANGROW_WANTGROW  = (GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW);
    public static final int CANGROW_CANSHRINK = (GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK);

    private UI() { }

    public static <T> @Nullable T invokeAndGet(@NotNull Callable<T> callable) {
        return invokeAndGet(false, callable);
    }

    public static <T> @Nullable T invokeAndGet(boolean propagateExceptions, @NotNull Callable<T> callable) {
        if(SwingUtilities.isEventDispatchThread()) return U.propagate(propagateExceptions, callable);

        return U.propagate(propagateExceptions, () -> {
            ObjectRef<T>         val = new ObjectRef<>();
            ObjectRef<Exception> err = new ObjectRef<>();
            SwingUtilities.invokeAndWait(() -> { try { val.value = callable.call(); } catch(Exception e) { err.value = e; } });
            if(err.value != null) throw err.value;
            return val.value;
        });
    }

    public static <T> Future<T> invokeAndGetLater(@NotNull Callable<T> callable) {
        if(SwingUtilities.isEventDispatchThread()) throw new RuntimeException("Cannot be called from the event dispatch thread.");
        return new UIFuture<>(callable);
    }

    public static void invokeAndWait(@NotNull Runnable runnable) {
        invokeAndWait(false, runnable);
    }

    public static void invokeAndWait(boolean propagateExceptions, @NotNull Runnable runnable) {
        try {
            if(SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            }
            else {
                ObjectRef<Exception> err = new ObjectRef<>();
                SwingUtilities.invokeAndWait(() -> { try { runnable.run(); } catch(Exception e) { err.value = e; } });
                if(propagateExceptions && (err.value != null)) throw U.makeRuntimeException(err.value);
            }
        }
        catch(Exception e) {
            if(propagateExceptions) throw U.makeRuntimeException(e);
        }
    }

    public static void invokeLater(@NotNull Runnable runnable) {
        if(SwingUtilities.isEventDispatchThread()) runnable.run();
        else SwingUtilities.invokeLater(runnable);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column) {
        return getGridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column, int anchor) {
        return getGridConstraints(row, column, 1, 1, anchor);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column, int rowSpan, int colSpan) {
        return getGridConstraints(row, column, rowSpan, colSpan, GridConstraints.ANCHOR_CENTER);
    }

    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column, int rowSpan, int colSpan, int anchor) {
        return getGridConstraints(row, column, rowSpan, colSpan, anchor, GridConstraints.FILL_BOTH);
    }

    @NotNull
    public static GridConstraints getGridConstraints(int row, int column, int rowSpan, int colSpan, int anchor, int fill) {
        return new GridConstraints(row, column, rowSpan, colSpan, anchor, fill, CANGROW_CANSHRINK, CANGROW_CANSHRINK, null, null, null, 0);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull GridConstraints getHSpacerConstraints(int row, int column) { return getHSpacerConstraints(row, column, 1, 1); }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getHSpacerConstraints(int row, int column, int rowSpan, int colSpan) {
        return new GridConstraints(row,
                                   column,
                                   rowSpan,
                                   colSpan,
                                   GridConstraints.ANCHOR_CENTER,
                                   GridConstraints.FILL_HORIZONTAL,
                                   CANGROW_WANTGROW,
                                   GridConstraints.SIZEPOLICY_FIXED,
                                   null,
                                   null,
                                   null,
                                   0,
                                   false);
    }

    public static @NotNull Icon getIcon(@NotNull String name, @NotNull Class<?> referenceClass) {
        try {
            return new ImageIcon(ImageIO.read(Objects.requireNonNull(referenceClass.getResourceAsStream(name))));
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull GridConstraints getVSpacerConstraints(int row, int column) { return getVSpacerConstraints(row, column, 1, 1); }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getVSpacerConstraints(int row, int column, int rowSpan, int colSpan) {
        return new GridConstraints(row,
                                   column,
                                   rowSpan,
                                   colSpan,
                                   GridConstraints.ANCHOR_CENTER,
                                   GridConstraints.FILL_VERTICAL,
                                   GridConstraints.SIZEPOLICY_FIXED,
                                   CANGROW_WANTGROW,
                                   null,
                                   null,
                                   null,
                                   0,
                                   false);
    }

    private static final class UIFuture<T> implements Future<T> {
        private final Callable<T> callable;
        private final Object      lockObject = UUID.randomUUID().toString();
        private       boolean     done       = false;
        private       Exception   error      = null;
        private       T           results    = null;

        public UIFuture(Callable<T> callable) {
            this.callable = callable;
            SwingUtilities.invokeLater(this::invokeCallable);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public @Nullable T get() throws InterruptedException, ExecutionException {
            synchronized(lockObject) {
                while(!done) lockObject.wait();
                return _get();
            }
        }

        @Contract(pure = true)
        @Override
        public @Nullable T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if(timeout < 0) throw new IllegalArgumentException("Timeout cannot be less than zero.");
            long millis = unit.toMillis(timeout);
            if(millis > TimeUnit.DAYS.toMillis(365)) throw new IllegalArgumentException("Timeout cannot be greater than one year.");

            synchronized(lockObject) {
                if(done) return _get();
                long when = (System.currentTimeMillis() + millis);
                do {
                    lockObject.wait(unit.toMillis(when - System.currentTimeMillis()));
                    if(done) return _get();
                    if(System.currentTimeMillis() >= when) throw new TimeoutException();
                }
                while(true);
            }
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            synchronized(lockObject) { return done; }
        }

        private T _get() throws ExecutionException {
            if(error != null) throw new ExecutionException(error);
            return results;
        }

        private void invokeCallable() {
            try { results = this.callable.call(); } catch(Exception e) { synchronized(lockObject) { error = e; } }
            synchronized(lockObject) {
                done = true;
                lockObject.notifyAll();
            }
        }
    }
}
