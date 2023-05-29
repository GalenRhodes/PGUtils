package com.projectgalen.lib.utils.errors;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Errors.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 29, 2023
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public final class Errors {
    private Errors() { }

    public static <T extends Throwable> @Nullable T findNestedCause(@NotNull Throwable t, @NotNull Class<T> cls) {
        return (cls.isInstance(t) ? cls.cast(t) : ((t.getCause() == null) ? null : findNestedCause(t.getCause(), cls)));
    }

    public static @NotNull <T extends Throwable> T getThrowable(@NotNull String msg, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class).newInstance(msg); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull RuntimeException makeRuntimeException(@NotNull Exception e) {
        return ((e instanceof RuntimeException) ? ((RuntimeException)e) : new RuntimeException(e));
    }

    public static <T> @Nullable T propagate(boolean propagateExceptions, @NotNull Callable<T> callable) {
        try {
            return callable.call();
        }
        catch(Exception e) {
            if(propagateExceptions) throw makeRuntimeException(e);
            return null;
        }
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@NotNull Throwable t, @NotNull Class<T> throwableClass) {
        return wrapThrowable(null, t, throwableClass);
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@Nullable String msg, @NotNull Throwable t, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class, Throwable.class).newInstance(((msg == null) ? t.getMessage() : msg), t); }
        catch(NoSuchMethodException | InstantiationException |
              IllegalAccessException |
              InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
}
