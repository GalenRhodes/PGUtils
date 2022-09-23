package com.projectgalen.lib.utils.delegates;

import org.jetbrains.annotations.NotNull;

public interface WithValueDelegate<P> {
    void action(@NotNull P value);
}
