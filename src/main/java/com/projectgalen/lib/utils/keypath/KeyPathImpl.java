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

import com.projectgalen.lib.utils.Null;
import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.reflection.Reflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class KeyPathImpl {

    private static final char             PATH_ELEM_SEP = '.';
    private static final PGResourceBundle msgs          = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props         = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);

    private KeyPathImpl() { }

    @Nullable
    public static <T> T getValueForKeyPath(@NotNull String keyPath, @Nullable Object target) {
        int idx = keyPath.lastIndexOf(PATH_ELEM_SEP);
        return ((idx < 0) ? getValueForKey(keyPath, target) : getValueForKey(keyPath.substring(idx + 1), getValueForKeyPath(keyPath.substring(0, idx), target)));
    }

    public static void setValueForKeyPath(@NotNull String keyPath, @Nullable Object value, @Nullable Object target) {
        int idx = keyPath.lastIndexOf(PATH_ELEM_SEP);
        setValueForKey(((idx < 0) ? keyPath : keyPath.substring(idx + 1)), value, ((idx < 0) ? target : getValueForKeyPath(keyPath.substring(0, idx), target)));
    }

    private static String getFieldNameFormat(int i) {
        return props.getProperty(String.format("keypath.field.format%d", i));
    }

    @Nullable
    private static Field getFieldNamed(@NotNull String name, @Nullable Class<?> cls) {
        return getFieldNamed(name, cls, null, false);
    }

    @Nullable
    private static Field getFieldNamed(@NotNull String name, @Nullable Class<?> cls, @Nullable Class<?> type, boolean exact) {
        if(cls == null) return null;
        for(Field f : cls.getDeclaredFields()) if(f.getName().equals(name)) if((type == null) || isAssignable(exact, f.getType(), type)) return f;
        return getFieldNamed(name, cls.getSuperclass(), type, exact);
    }

    private static <T> T getFieldValue(@NotNull Field field, @NotNull Object target, @NotNull String key) {
        try {
            field.setAccessible(true);
            return (T)field.get(target);
        }
        catch(Exception e) {
            throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_getting_field", key, target.getClass().getName()), e);
        }
    }

    private static <T> T getFromGetter(@NotNull String key, @NotNull Object target, Class<?> tClass) {
        int    i   = 0;
        String fmt = getGetterNameFormat(++i);

        while(fmt != null) {
            Method method = getGetterNamed(String.format(fmt, key.charAt(0), key.substring(1), key), tClass);
            if(method != null) return invokeGetter(method, target, key);
            fmt = getGetterNameFormat(++i);
        }

        throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_not_found", key, tClass.getName()));
    }

    private static String getGetterNameFormat(int i) {
        return props.getProperty(String.format("keypath.getter.format%d", i));
    }

    @Nullable
    private static Method getGetterNamed(@NotNull String name, @Nullable Class<?> cls) {
        if(cls == null) return null;
        for(Method m : cls.getDeclaredMethods()) if((m.getReturnType() != void.class) && (m.getParameterCount() == 0) && m.getName().equals(name)) return m;
        return getGetterNamed(name, cls.getSuperclass());
    }

    private static String getSetterNameFormat(int i) {
        return props.getProperty(String.format("keypath.setter.format%d", i));
    }

    @Nullable
    private static Method getSetterNamed(@NotNull String name, @Nullable Class<?> cls, @Nullable Class<?> paramCls, boolean exact) {
        if(cls == null) return null;
        for(Method m : cls.getDeclaredMethods()) {
            Class<?>[] params = m.getParameterTypes();
            if((m.getReturnType() == void.class) && (params.length == 1) && m.getName().equals(name) && ((paramCls == null) || isAssignable(exact, params[0], paramCls))) return m;
        }
        return getSetterNamed(name, cls.getSuperclass(), paramCls, exact);
    }

    private static <T> T getValueForKey(@NotNull String key, @Nullable Object target) {
        if(target == null) return null;
        if(key.length() == 0) throw new KeyPathException(msgs.getString("msg.err.reflect.keypath.elem_empty"));
        return ((target instanceof Map) ? ((Map<Object, T>)target).get(key) : getValueForKey(key, target, target.getClass()));
    }

    private static <T> T getValueForKey(@NotNull String key, @NotNull Object target, Class<?> cls) {
        int    i   = 0;
        String fmt = getFieldNameFormat(++i);

        while(fmt != null) {
            Field field = getFieldNamed(String.format(fmt, key), cls);
            if(field != null) return getFieldValue(field, target, key);
            fmt = getFieldNameFormat(++i);
        }

        return getFromGetter(key, target, cls);
    }

    private static <T> T invokeGetter(@NotNull Method getterMethod, @NotNull Object target, @NotNull String key) {
        try {
            getterMethod.setAccessible(true);
            return (T)getterMethod.invoke(target);
        }
        catch(Exception e) {
            throw new KeyPathException(msgs.format("msg.err.reflect.keypath.elem_error_invoking_getter", key, target.getClass().getName()), e);
        }
    }

    private static boolean invokeSetter(@NotNull Method setterMethod, @Nullable Object value, @NotNull Object target, @NotNull String key) {
        try {
            setterMethod.setAccessible(true);
            setterMethod.invoke(target, value);
            return true;
        }
        catch(Exception e) {
            String mkey = ((value == null) ? "msg.err.reflect.keypath.elem_error_invoking_setter_null" : "msg.err.reflect.keypath.elem_error_invoking_setter");
            throw new KeyPathException(msgs.format(mkey, key, target.getClass().getName(), Null.getIfNotNull(value, Object::getClass)));
        }
    }

    private static boolean isAssignable(boolean exact, Class<?> lht, @NotNull Class<?> rht) {
        Class<?> _lht = Reflection.objectClassForPrimitive(lht);
        Class<?> _rht = Reflection.objectClassForPrimitive(rht);
        return (exact ? (_lht == _rht) : (_lht.isAssignableFrom(_rht) || Reflection.isNumericallyAssignable(_lht, _rht)));
    }

    private static boolean setFieldValue(@NotNull Field field, @Nullable Object value, @NotNull Object target, @NotNull String key) {
        try {
            field.setAccessible(true);
            field.set(target, value);
            return true;
        }
        catch(Exception e) {
            String mKey = ((value == null) ? "msg.err.reflect.keypath.elem_error_setting_field_null" : "msg.err.reflect.keypath.elem_error_setting_field");
            throw new KeyPathException(msgs.format(mKey, key, target.getClass().getName(), Null.getIfNotNull(value, Object::getClass)));
        }
    }

    private static void setValueForKey(@NotNull String key, @Nullable Object value, @Nullable Object target) {
        if(target != null) {
            if(target instanceof Map) { ((Map<Object, Object>)target).put(key, value); }
            else if(!setValueForKey(key, value, target, true)) setValueForKey(key, value, target, false);
        }
    }

    private static boolean setValueForKey(@NotNull String key, @Nullable Object value, @NotNull Object target, boolean exact) {
        return (setViaField(key, value, target, exact) || setViaSetter(key, value, target, exact));
    }

    private static boolean setViaField(@NotNull String key, @Nullable Object value, @NotNull Object target, boolean exact) {
        int    i   = 0;
        String fmt = getFieldNameFormat(++i);

        while(fmt != null) {
            Field field = getFieldNamed(key, target.getClass(), Null.getIfNotNull(value, Object::getClass), exact);
            if(field != null) return setFieldValue(field, Reflection.castIfNumeric(value, field.getType()), target, key);
            fmt = getFieldNameFormat(++i);
        }

        return false;
    }

    private static boolean setViaSetter(@NotNull String key, @Nullable Object value, @NotNull Object target, boolean exact) {
        int    i   = 0;
        String fmt = getSetterNameFormat(++i);

        while(fmt != null) {
            Method method = getSetterNamed(key, target.getClass(), Null.getIfNotNull(value, Object::getClass), exact);
            if(method != null) return invokeSetter(method, Reflection.castIfNumeric(value, method.getParameterTypes()[0]), target, key);
            fmt = getSetterNameFormat(++i);
        }

        return false;
    }
}
