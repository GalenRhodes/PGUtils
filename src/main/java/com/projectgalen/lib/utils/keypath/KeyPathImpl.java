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
import com.projectgalen.lib.utils.Range;
import com.projectgalen.lib.utils.reflection.Reflection;
import com.projectgalen.lib.utils.regex.Regex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;

public final class KeyPathImpl {

    private static final PGResourceBundle msgs  = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);

    private KeyPathImpl() { }

    public static <T> T getValueForKey(@NotNull Class<T> type, @NotNull String key, Object source) {
        return ((source == null) ? null : _getValueForKey(type, key.trim(), source));
    }

    public static <T> T getValueForKeyPath(@NotNull Class<T> type, @NotNull String keyPath, Object source) {
        return ((source == null) ? null : _getValueForKeyPath(type, keyPath.trim(), source));
    }

    public static void setValueForKey(@NotNull String key, Object target, Object value) {
        if(target != null) _setValueForKey(key.trim(), target, value);
    }

    public static void setValueForKeyPath(@NotNull String keyPath, Object target, Object value) {
        Range range = Regex.rangeOfLastMatch(props.getProperty("keypath.separator.regexp"), keyPath);
        if(range == null) setValueForKey(keyPath, target, value);
        else setValueForKey(keyPath.substring(range.end), getValueForKeyPath(Object.class, keyPath.substring(0, range.start), target), value);
    }

    private static Field _getField(@NotNull String key, @NotNull Object obj) {
        Class<?> objClass = obj.getClass();

        for(int i = 1, j = props.getInt("keypath.field.format.count"); i <= j; i++) {
            Field field = Reflection.getAccessibleFieldOrNull(objClass, props.getProperty(String.format("keypath.field.format%d", i)));
            if(field != null) return field;
        }

        return null;
    }

    private static Method _getMethod(@NotNull String methodType, @NotNull String key, @NotNull Object obj) {
        Class<?> objClass = obj.getClass();

        for(int i = 1, j = props.getInt(String.format("keypath.%s.format.count", methodType)); i <= j; i++) {
            Method method = Reflection.getAccessibleMethodOrNull(objClass, props.getProperty(String.format("keypath.%s.format%d", methodType, i)));
            if(method != null) return method;
        }

        return null;
    }

    private static <T> T _getValueForKey(@NotNull Class<T> type, @NotNull String key, Object source) {
        if(key.length() == 0) throw new KeyPathException(msgs.getString("msg.err.reflect.keypath.key_empty"));

        Field field = _getField(key, source);
        if(field != null) {
            try {
                return type.cast(field.get(source));
            }
            catch(Throwable t) {
                throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_getting_field", key, source.getClass().getName()), t);
            }
        }

        Method getter = _getMethod("getter", key, source);
        if(getter != null) {
            try {
                return type.cast(getter.invoke(source));
            }
            catch(Throwable t) {
                throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_invoking_getter", key, source.getClass().getName()), t);
            }
        }

        throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_not_found", key, source.getClass().getName()));
    }

    private static @Nullable <T> T _getValueForKeyPath(@NotNull Class<T> type, @NotNull String keyPath, Object source) {
        if(keyPath.length() == 0) throw new KeyPathException(msgs.getString("msg.err.reflect.keypath.key_path_empty"));

        Matcher m       = Regex.getMatcher(props.getProperty("keypath.separator.regexp"), keyPath);
        int     lastIdx = 0;

        while(m.find()) {
            String _key = keyPath.substring(lastIdx, m.start());
            if(_key.length() == 0) throw new KeyPathException(msgs.getString("msg.err.reflect.keypath.elem_empty"));
            source = _getValueForKey(Object.class, _key, source);
            if(source == null) return null;
            lastIdx = m.end();
        }

        String _key = keyPath.substring(lastIdx);
        if(_key.length() == 0) throw new KeyPathException(msgs.getString("msg.err.reflect.keypath.elem_empty"));
        return _getValueForKey(type, _key, source);
    }

    private static void _setValueForKey(@NotNull String key, Object target, Object value) {
        if(key.length() == 0) throw new KeyPathException(msgs.getString("msg.err.reflect.keypath.key_empty"));

        Field field = _getField(key, target);
        if(field != null) {
            try {
                field.set(target, value);
            }
            catch(Throwable t) {
                if(value == null) throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_setting_field_null", key, target.getClass().getName()), t);
                throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_setting_field", key, target.getClass().getName(), value.getClass().getName()), t);
            }
        }

        Method setter = _getMethod("setter", key, target);
        if(setter != null) {
            try {
                setter.invoke(target, value);
            }
            catch(Throwable t) {
                if(value == null) throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_invoking_setter_null", key, target.getClass().getName()), t);
                throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_invoking_setter", key, target.getClass().getName(), value.getClass().getName()), t);
            }
        }

        throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_not_found", key, target.getClass().getName()));
    }
}
