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
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Stream;

import static com.projectgalen.lib.utils.PGProperties.DEFAULT_LIST_SEPARATOR_PATTERN;
import static com.projectgalen.lib.utils.PGProperties.DEFAULT_MAP_KV_PATTERN;

public final class PGResourceBundle extends ResourceBundle {

    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    private final ResourceBundle bundle;

    private PGResourceBundle(@NotNull ResourceBundle bundle) {
        super();
        this.bundle = bundle;
    }

    public @NotNull String format(String key, Object... args) {
        return String.format(getString(key), args);
    }

    public @NotNull String format(boolean macroExpansion, @NotNull String key, Object... args) {
        return String.format(getString(key, macroExpansion), args);
    }

    public @Override @NotNull Enumeration<String> getKeys() {
        return bundle.getKeys();
    }

    public @NotNull Stream<String> getStreamOf(@NotNull @NonNls String key) {
        return getStreamOf(key, DEFAULT_LIST_SEPARATOR_PATTERN);
    }

    public @NotNull Stream<String> getStreamOf(@NotNull @NonNls String key, @NotNull @RegExp @Language("RegExp") @NonNls String regexp) {
        return Streams.splitStream(U.tr(getStringQuietly(key)), regexp);
    }

    public @NotNull String getString(@NotNull String key, boolean macroExpansion) {
        return (macroExpansion ? bundle.getString(key) : getString(key));
    }

    public @NotNull String getString(@NotNull String key, @NotNull String defaultValue) {
        return getString(key, defaultValue, true);
    }

    public @NotNull String getString(@NotNull String key, @NotNull String defaultValue, boolean macroExpansion) {
        try {
            return getString(key);
        }
        catch(MissingResourceException e) {
            return (macroExpansion ? Macro.replaceMacros(defaultValue, this::getStringQuietly) : defaultValue);
        }
    }

    public @NotNull List<String> getStringList(@NotNull String key) {
        return getStringList(key, DEFAULT_LIST_SEPARATOR_PATTERN);
    }

    public @NotNull List<String> getStringList(@NotNull String key, @NotNull @RegExp @Language("RegExp") String regexp) {
        return new ArrayList<>(Arrays.asList(getString(key).trim().split(regexp)));
    }

    public @NotNull Map<String, String> getStringMap(@NotNull String key) {
        return getStringMap(key, DEFAULT_LIST_SEPARATOR_PATTERN, DEFAULT_MAP_KV_PATTERN);
    }

    public @NotNull Map<String, String> getStringMap(@NotNull String key, @NotNull @RegExp @Language("RegExp") String listRegexp, @NotNull @RegExp @Language("RegExp") String kvRegexp) {
        Map<String, String> map = new LinkedHashMap<>();
        for(String s : getStringList(key, listRegexp)) {
            String[] kv = s.split(kvRegexp, 2);
            if(kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    public @Nullable String getStringQuietly(@NotNull String key) {
        try {
            return bundle.getString(key);
        }
        catch(MissingResourceException e) {
            return null;
        }
    }

    protected @Override @Unmodifiable @Nullable Object handleGetObject(@NotNull String key) {
        try {
            return Macro.replaceMacros(bundle.getString(key), this::getStringQuietly);
        }
        catch(MissingResourceException ignore) {
            return null;
        }
    }

    @Contract("_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName));
    }

    @Contract("_,_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName, Module module) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, module));
    }

    @Contract("_,_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName, Locale locale) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, locale));
    }

    @Contract("_,_,_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName, Locale locale, Module module) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, locale, module));
    }

    @Contract("_,_,_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName, Locale locale, ClassLoader loader) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, locale, loader));
    }

    @Contract("_,_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName, Control control) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, control));
    }

    @Contract("_,_,_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName, Locale locale, Control control) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, locale, control));
    }

    @Contract("_,_,_,_ -> new")
    public static @NotNull PGResourceBundle getPGBundle(@NotNull String baseName, Locale locale, ClassLoader loader, Control control) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, locale, loader, control));
    }

    @Contract("_ -> new")
    public static @NotNull PGResourceBundle getXMLPGBundle(@NotNull String baseName) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, new XMLResourceBundleControl()));
    }

    @Contract("_,_ -> new")
    public static @NotNull PGResourceBundle getXMLPGBundle(@NotNull String baseName, Locale locale) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, locale, new XMLResourceBundleControl()));
    }

    @Contract("_,_,_ -> new")
    public static @NotNull PGResourceBundle getXMLPGBundle(@NotNull String baseName, Locale locale, ClassLoader loader) {
        return new PGResourceBundle(ResourceBundle.getBundle(baseName, locale, loader, new XMLResourceBundleControl()));
    }

    private static class XMLKeyEnumerator implements Enumeration<String> {
        private final Enumeration<Object> e;

        public XMLKeyEnumerator(@NotNull Enumeration<Object> e) {
            this.e = e;
        }

        public @Override boolean hasMoreElements() {
            return e.hasMoreElements();
        }

        public @Override String nextElement() {
            return Objects.toString(e.nextElement(), null);
        }
    }

    private static class XMLResourceBundle extends ResourceBundle {
        private static final Properties props = new Properties();

        private XMLResourceBundle(InputStream inputStream) {
            super();
            try {
                props.loadFromXML(inputStream);
            }
            catch(Exception e) {
                throw new MissingResourceException(msgs.getString("msg.err.bundle.missing.resource.file"), PGResourceBundle.class.getName(), "");
            }
        }

        public @NotNull @Override Enumeration<String> getKeys() {
            return new XMLKeyEnumerator(props.keys());
        }

        protected @Override @Unmodifiable Object handleGetObject(@NotNull String key) {
            return props.getProperty(key);
        }
    }

    private static class XMLResourceBundleControl extends ResourceBundle.Control {
        public XMLResourceBundleControl() { }

        @Override
        @NotNull
        @Unmodifiable
        @Contract("null -> fail")
        public List<String> getFormats(String baseName) {
            if(baseName == null) throw new NullPointerException();
            return Collections.singletonList("xml");
        }

        @Override
        @Nullable
        @Contract("null, _, _, _, _ -> fail; !null, null, _, _, _ -> fail; !null, !null, null, _, _ -> fail; !null, !null, !null, null, _ -> fail")
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if(baseName == null || locale == null || format == null || loader == null) throw new NullPointerException();
            if(!format.equals("xml")) return null;
            URL url = loader.getResource(toResourceName(toBundleName(baseName, locale), format));
            if(url == null) return null;
            URLConnection conn = url.openConnection();
            conn.setUseCaches(!reload);
            try(BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream())) { return new XMLResourceBundle(inputStream); }
        }
    }
}
