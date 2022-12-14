package com.projectgalen.lib.utils.delegates;

import org.jetbrains.annotations.NotNull;

public interface GetWithValueDelegate<P, R> {
    R action(@NotNull P value);
}
