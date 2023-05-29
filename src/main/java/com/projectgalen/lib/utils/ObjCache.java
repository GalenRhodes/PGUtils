package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: ObjCache.java
//         IDE: IntelliJ
//      AUTHOR: Galen Rhodes
//        DATE: January 05, 2023
//
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

import com.projectgalen.lib.utils.concurrency.Locks;
import org.jetbrains.annotations.NotNull;

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

    public <T> T get(@NotNull String key, @NotNull Class<T> cls) {
        return Locks.getWithLock(lock, () -> cls.cast(cache.get(key)));
    }

    public Object get(@NotNull String key) {
        return Locks.getWithLock(lock, () -> cache.get(key));
    }

    public <T> T remove(@NotNull String key, @NotNull Class<T> cls) {
        return Locks.getWithLock(lock, () -> cls.cast(cache.remove(key)));
    }

    public Object remove(@NotNull String key) {
        return Locks.getWithLock(lock, () -> cache.remove(key));
    }

    public <T> T store(@NotNull String key, @NotNull T obj) {
        return Locks.getWithLock(lock, () -> ((Class<T>)obj.getClass()).cast(cache.put(key, obj)));
    }

    public static ObjCache getInstance() {
        return ObjCacheHolder.INSTANCE;
    }

    private static final class ObjCacheHolder {
        private static final ObjCache INSTANCE = new ObjCache();
    }
}
