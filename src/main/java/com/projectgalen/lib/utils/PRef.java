package com.projectgalen.lib.utils;

public class PRef<T> {

    protected T value;

    public PRef() {
        this.value = null;
    }

    public PRef(T initialValue) {
        this.value = initialValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
