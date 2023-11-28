package com.projectgalen.lib.utils.keypath;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: KeyPathImpl.java
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

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.errors.KeyPathException;
import com.projectgalen.lib.utils.text.Text;
import com.projectgalen.lib.utils.text.regex.Regex;
import com.projectgalen.lib.utils.tuples.Two;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;

@SuppressWarnings("unused")
public final class KeyPathImpl {

    private static final                     PGResourceBundle msgs  = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final                     PGProperties     props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);
    private static final @Language("RegExp") String           REGEX = props.getProperty("keypath.separator.regexp");

    private KeyPathImpl() { }

    public static <T> T getValueForKey(@NotNull Class<T> type, @NotNull String key, Object source) {
        if(source == null) return null;
        Class<?> cls = source.getClass();
        Field    fld = findFieldForReading(cls, key, type);
        if(fld == null) fld = findFieldForReading(cls, "_" + key, type);
        if(fld != null) return getFieldValue(type, source, fld);

        Method method = findGetter(cls, String.format("get%C%s", key.charAt(0), key.substring(1)));
        if(method == null) method = findGetter(cls, key);
        if(method == null) method = findGetter(cls, String.format("_get%C%s", key.charAt(0), key.substring(1)));
        if(method == null) method = findGetter(cls, "_" + key);
        if(method != null) return getMethodValue(type, source, method);

        throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_not_found", key, source.getClass().getName()));
    }

    public static <T> T getValueForKeyPath(@NotNull Class<T> type, @NotNull String keyPath, Object source) {
        if(source == null) return null;
        Two<Object, String> x = foo01(keyPath, source);
        return getValueForKey(type, x.u(), x.t());
    }

    public static void setValueForKeyPath(@NotNull String keyPath, Object target, Object value) {

    }

    public static String @NotNull [] splitPath(@NotNull String keyPath) {
        String[] path = keyPath.split(REGEX);
        int      len  = (path.length - 1);
        if(len == 0) return new String[] { "", path[0] };
        return new String[] { Text.join(path, 0, len, "."), path[len] };
    }

    private static @Nullable Field findFieldForReading(Class<?> cls, String name, Class<?> type) {
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) if(name.equals(field.getName()) && type.isAssignableFrom(field.getType())) return field;
            cls = cls.getSuperclass();
        }
        return null;
    }

    private static @Nullable Field findFieldForWriting(Class<?> cls, String name, Class<?> type) {
        while(cls != null) {
            for(Field field : cls.getDeclaredFields()) if(name.equals(field.getName()) && field.getType().isAssignableFrom(type)) return field;
            cls = cls.getSuperclass();
        }
        return null;
    }

    private static @Nullable Method findGetter(Class<?> cls, String name) {
        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) if((m.getParameterCount() == 0) && isNotVoid(m.getReturnType()) && name.equals(m.getName())) return m;
            cls = cls.getSuperclass();
        }
        return null;
    }

    private static @NotNull Two<Object, String> foo01(@NotNull String keyPath, Object source) {
        Matcher m    = Regex.getMatcher(REGEX, keyPath);
        int     last = 0;

        while(m.find()) {
            if(source != null) source = getValueForKey(Object.class, keyPath.substring(last, m.start()), source);
            last = m.end();
        }

        return new Two<>(source, keyPath.substring(last));
    }

    private static <T> T getFieldValue(@NotNull Class<T> type, @NotNull Object source, @NotNull Field field) {
        try {
            field.setAccessible(true);
            return type.cast(field.get(source));
        }
        catch(IllegalAccessException e) {
            throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_getting_field", field.getName(), source.getClass().getName()), e);
        }
    }

    private static <T> T getMethodValue(@NotNull Class<T> type, @NotNull Object source, @NotNull Method method) {
        try {
            method.setAccessible(true);
            return type.cast(method.invoke(source));
        }
        catch(IllegalAccessException | InvocationTargetException e) {
            throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_invoking_getter", method.getName(), source.getClass().getName()), e);
        }
    }

    private static boolean isNotVoid(@NotNull Class<?> cls) {
        return ((cls != void.class) && (cls != Void.class));
    }

    private static boolean isVoid(@NotNull Class<?> cls) {
        return ((cls == void.class) || (cls == Void.class));
    }
}
