package com.projectgalen.lib.utils;

import com.projectgalen.lib.utils.concurrency.Locks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({ "UnusedReturnValue", "unused", "unchecked" })
public class ObjCache {

    private final Map<String, Object> cache = new TreeMap<>();
    private final Lock                lock  = new ReentrantLock(true);

    public ObjCache() {
        super();
    }

    public @Nullable <T> T get(@NotNull String key, @NotNull Class<T> cls) {
        return Locks.getWithLock(lock, () -> cls.cast(cache.get(key)));
    }

    public @Nullable Object get(@NotNull String key) {
        return Locks.getWithLock(lock, () -> cache.get(key));
    }

    public @Nullable <T> T remove(@NotNull String key, @NotNull Class<T> cls) {
        return Locks.getWithLock(lock, () -> cls.cast(cache.remove(key)));
    }

    public @Nullable Object remove(@NotNull String key) {
        return Locks.getWithLock(lock, () -> cache.remove(key));
    }

    public @Nullable <T> T store(@NotNull String key, @NotNull T obj) {
        return Locks.getWithLock(lock, () -> ((Class<T>)obj.getClass()).cast(cache.put(key, obj)));
    }

    public static ObjCache getInstance() {
        return ObjCacheHolder.INSTANCE;
    }

    private static final class ObjCacheHolder {
        private static final ObjCache INSTANCE = new ObjCache();
    }
}
