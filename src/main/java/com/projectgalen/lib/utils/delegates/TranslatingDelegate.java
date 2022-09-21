package com.projectgalen.lib.utils.delegates;

public interface TranslatingDelegate<P, R> {
    R translate(P arg);
}
