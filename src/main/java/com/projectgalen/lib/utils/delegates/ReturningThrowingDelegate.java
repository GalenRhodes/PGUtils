package com.projectgalen.lib.utils.delegates;

public interface ReturningThrowingDelegate<T> {
    T action() throws Exception;
}
