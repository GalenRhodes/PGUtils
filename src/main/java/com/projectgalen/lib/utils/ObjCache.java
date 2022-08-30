package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public class ObjCache {

    private final Map<String, Object> cache = new TreeMap<>();
    private final Lock                lock  = new ReentrantLock(true);

    public ObjCache() {
        super();
    }

    public @Nullable <T> T get(@NotNull String key, @NotNull Class<T> cls) {
        lock.lock();
        try { return cls.cast(cache.get(key)); } finally { lock.unlock(); }
    }

    public @Nullable Object get(@NotNull String key) {
        lock.lock();
        try { return cache.get(key); } finally { lock.unlock(); }
    }

    public @Nullable <T> T remove(@NotNull String key, @NotNull Class<T> cls) {
        lock.lock();
        try { return cls.cast(cache.remove(key)); } finally { lock.unlock(); }
    }

    public @Nullable Object remove(@NotNull String key) {
        lock.lock();
        try { return cache.remove(key); } finally { lock.unlock(); }
    }

    public @Nullable <T> T store(@NotNull String key, @NotNull T obj) {
        lock.lock();
        try {
            //noinspection unchecked
            return ((Class<T>)obj.getClass()).cast(cache.put(key, obj));
        }
        finally { lock.unlock(); }
    }

    public static ObjCache getInstance() {
        return ObjCacheHolder.INSTANCE;
    }

    private static final class ObjCacheHolder {
        private static final ObjCache INSTANCE = new ObjCache();
    }
}
