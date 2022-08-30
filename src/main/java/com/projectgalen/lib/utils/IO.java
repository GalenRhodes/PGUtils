package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

public class IO {
    private IO() {
    }

    public static void closeQuietly(@NotNull Closeable closeable) {
        try { closeable.close(); } catch(Exception ignore) { }
    }
}
