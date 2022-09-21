package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Null {
    private Null() {
    }

    public @Override String toString() {
        return "null";
    }

    public static Null NULL() {
        return NullHolder.INSTANCE;
    }

    public static <T> T get(@NotNull Object o) {
        if(NULL().equals(o)) return null;
        //noinspection unchecked
        return (T)o;
    }

    public static @NotNull Object set(@Nullable Object o) {
        return U.ifNull(o, NULL());
    }

    private static final class NullHolder {
        private static final Null INSTANCE = new Null();
    }
}
