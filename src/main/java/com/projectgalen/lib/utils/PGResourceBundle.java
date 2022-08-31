package com.projectgalen.lib.utils;

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
