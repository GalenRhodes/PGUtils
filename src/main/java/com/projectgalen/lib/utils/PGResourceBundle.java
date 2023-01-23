package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGResourceBundle.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: January 23, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import com.projectgalen.lib.utils.macro.Macro;
import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class PGResourceBundle extends ResourceBundle {

    private final ResourceBundle bundle;

    private PGResourceBundle(@NotNull String bundleName) {
        bundle = ResourceBundle.getBundle(bundleName);
    }

    public final String format(String key, Object... args) {
        return String.format(getString(key), args);
    }

    @Override
    protected Object handleGetObject(@NotNull String key) {
        try { return Macro.replaceMacros(bundle.getString(key), bundle::getString); }
        catch(Exception ignore) { return null; }
    }

    @NotNull
    @Override
    public Enumeration<String> getKeys() {
        return bundle.getKeys();
    }

    public static synchronized PGResourceBundle getSharedBundle(@NotNull String bundleName) {
        PGResourceBundle bundle = CacheHolder.CACHE.get(bundleName, PGResourceBundle.class);
        if(bundle != null) return bundle;
        bundle = new PGResourceBundle(bundleName);
        CacheHolder.CACHE.store(bundleName, bundle);
        return bundle;
    }

    private static final class CacheHolder {
        private static final ObjCache CACHE = new ObjCache();
    }
}
