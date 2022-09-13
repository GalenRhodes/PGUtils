package com.projectgalen.lib.utils;

public class Null {
    private Null() {
    }

    @Override public String toString() {
        return "null";
    }

    public static Null NULL() {
        return NullHolder.INSTANCE;
    }

    private static final class NullHolder {
        private static final Null INSTANCE = new Null();
    }
}
