package com.projectgalen.lib.utils.macro;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MacroDelegate {
    @Nullable String getMacroValue(@NotNull String macroName);
}
