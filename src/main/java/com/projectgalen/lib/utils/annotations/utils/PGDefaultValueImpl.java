package com.projectgalen.lib.utils.annotations.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: DefaultValueImpl.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: February 24, 2023
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

import com.projectgalen.lib.utils.Dates;
import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.annotations.PGDefaultValue;
import com.projectgalen.lib.utils.errors.PGDefaultValueError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class PGDefaultValueImpl {
    private static final PGResourceBundle msgs                 = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props                = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);
    private static final String           VALUE_OF_METHOD_NAME = props.getProperty("default.value_of.method_name");

    private PGDefaultValueImpl() { }

    public static @NotNull Object getDefaultValue(@NotNull PGDefaultValue dv, @NotNull Class<?> type) throws PGDefaultValueError {
        if((dv.value().length() == 0) && !String.class.isAssignableFrom(type)) throw new PGDefaultValueError(msgs.getString("msg.err.def.val.empty_string"));
        try {
            if(String.class.isAssignableFrom(type)) return dv.value();
            else if(Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)) return getDate(dv, type);
            else if(Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) return Boolean.valueOf(dv.value());
            else if(Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type)) return dv.value().charAt(0);
            return getDefaultValueByValueOf(dv, type);
        }
        catch(PGDefaultValueError e) { throw e; }
        catch(Throwable e) { throw new PGDefaultValueError(msgs.format("msg.err.def.exception", e), e); }
    }

    public static void populate(@NotNull Method method, @Nullable Object obj) throws PGDefaultValueError {
        Class<?>[] params = method.getParameterTypes();
        if((method.getReturnType() != Void.class) || (params.length != 1)) throw new PGDefaultValueError(msgs.getString("msg.err.def.not_setter"));
        PGDefaultValue dv = method.getAnnotation(PGDefaultValue.class);
        if((obj != null) && (dv != null)) {
            try { method.invoke(obj, getDefaultValue(dv, params[0])); }
            catch(InvocationTargetException | IllegalAccessException e) {
                throw new PGDefaultValueError(msgs.format("msg.err.def.exception",
                                                          e), e);
            }
        }
    }

    public static void populate(@NotNull Field field, @Nullable Object obj) throws PGDefaultValueError {
        PGDefaultValue dv = field.getAnnotation(PGDefaultValue.class);
        if((obj != null) && (dv != null)) {
            try { field.set(obj, getDefaultValue(dv, field.getType())); } catch(IllegalAccessException e) { throw new PGDefaultValueError(msgs.format("msg.err.def.exception", e), e); }
        }
    }

    private static @NotNull Object dateByMilliseconds(@NotNull Class<?> type, long ms) {
        if(Calendar.class.isAssignableFrom(type)) return Dates.getCalendar(ms);
        else if(Timestamp.class.isAssignableFrom(type)) return new Timestamp(ms);
        else if(java.sql.Date.class.isAssignableFrom(type)) return new java.sql.Date(ms);
        else if(Time.class.isAssignableFrom(type)) return new Time(ms);
        return new Date(ms);
    }

    private static @NotNull Object getDate(@NotNull PGDefaultValue dv, @NotNull Class<?> type) throws ParseException {
        return dateByMilliseconds(type, ((dv.format().length() == 0) ? Long.parseLong(dv.value()) : new SimpleDateFormat(dv.format()).parse(dv.value()).getTime()));
    }

    private static @NotNull Object getDefaultByConstructor(@NotNull PGDefaultValue dv, @NotNull Class<?> type) throws PGDefaultValueError {
        try { return type.getConstructor(String.class).newInstance(dv.value()); }
        catch(NoSuchMethodException e) {
            throw new PGDefaultValueError(msgs.format("msg.err.def.no_way",
                                                      VALUE_OF_METHOD_NAME));
        }
        catch(Exception e) { throw new PGDefaultValueError(msgs.format("msg.err.def.exception", e), e); }
    }

    private static @NotNull Object getDefaultValueByValueOf(@NotNull PGDefaultValue dv, @NotNull Class<?> type) throws PGDefaultValueError {
        try {
            Object obj = type.getMethod(VALUE_OF_METHOD_NAME, String.class).invoke(null, dv.value());
            if(obj == null) throw new PGDefaultValueError(msgs.format("msg.err.def.value_of.returned_null", VALUE_OF_METHOD_NAME));
            return obj;
        }
        catch(PGDefaultValueError e) { throw e; }
        catch(NoSuchMethodException e) { return getDefaultByConstructor(dv, type); }
        catch(Exception e) {
            throw new PGDefaultValueError(msgs.format("msg.err.def.exception",
                                                      e), e);
        }
    }
}
