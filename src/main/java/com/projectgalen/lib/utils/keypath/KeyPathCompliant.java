package com.projectgalen.lib.utils.keypath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KeyPathCompliant {

    boolean doesKeyPathExist(@NotNull String keyPath);

    @Nullable <T> T getValueForKeyPath(@NotNull String keyPath);

    void setValueForKeyPath(@NotNull String keyPath, @Nullable Object value);
}
