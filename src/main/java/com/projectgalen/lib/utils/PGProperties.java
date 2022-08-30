package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("unused")
public class PGProperties extends Properties {

    public <T extends Throwable> PGProperties(@Nullable InputStream inputStream, Class<T> errorClass) throws T {
        super();
        try {
            if(inputStream == null) throw U.getThrowable("No input stream.", errorClass);
            try(inputStream) { load(inputStream); }
        }
        catch(Exception e) { throw U.wrapThrowable("Unable to load properties.", e, errorClass); }
    }

    public PGProperties(@Nullable InputStream inputStream) throws IllegalArgumentException {
        this(inputStream, IllegalArgumentException.class);
    }

    public static @NotNull PGProperties getSharedInstanceForNamedResource(@NotNull String resourceName, @NotNull Class<?> refClass) {
        String       key   = String.format("%s|%s", refClass.getName(), resourceName);
        PGProperties props = CacheHolder.CACHE.get(key, PGProperties.class);

        if(props != null) return props;
        props = new PGProperties(refClass.getResourceAsStream(resourceName));
        CacheHolder.CACHE.store(key, props);
        return props;
    }

    public static @NotNull PGProperties getSharedInstanceForNamedResource(@NotNull String resourceName) {
        return getSharedInstanceForNamedResource(resourceName.startsWith("/") ? resourceName : "/" + resourceName, PGProperties.class);
    }

    private static final class CacheHolder {
        private static final ObjCache CACHE = new ObjCache();
    }
}
