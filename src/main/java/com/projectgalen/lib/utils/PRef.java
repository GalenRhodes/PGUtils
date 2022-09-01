package com.projectgalen.lib.utils;

import org.jetbrains.annotations.Nullable;

public class PRef<T> {

    protected @Nullable T value;

    public PRef() {
        this.value = null;
    }

    public PRef(@Nullable T initialValue) {
        this.value = initialValue;
    }

    public @Nullable T getValue() {
        return value;
    }

    public void setValue(@Nullable T value) {
        this.value = value;
    }
}
