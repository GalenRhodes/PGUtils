package com.projectgalen.lib.utils.reflection;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGProxy.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 17, 2023
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@SuppressWarnings("unused")
public final class PGProxy {
    private PGProxy() { }

    @Contract("_, _ -> new")
    public static @NotNull ProxyInfo getProxyForClassName(@NotNull String className, @NotNull InvocationHandler proxyHandler) {
        try {
            Class<?> listenerClass = Class.forName(className);
            Object   listener      = Proxy.newProxyInstance(null, new Class<?>[]{ listenerClass }, proxyHandler);
            return new ProxyInfo(listener, listenerClass);
        }
        catch(Exception e) {
            if(e instanceof RuntimeException) throw (RuntimeException)e;
            throw new RuntimeException(e);
        }
    }

    public static class ProxyInfo {
        public final Object   proxy;
        public final Class<?> proxyType;

        public ProxyInfo(Object proxy, Class<?> proxyType) {
            this.proxy = proxy;
            this.proxyType = proxyType;
        }
    }
}
