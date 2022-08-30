package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;

@SuppressWarnings("unused")
public class U {

    private U() {
    }

    public static @NotNull String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static @NotNull <T extends Throwable> T getThrowable(@NotNull String msg, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class).newInstance(msg); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull Timestamp getTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static @NotNull <T> T ifNull(@Nullable T obj, @NotNull T defaultValue) {
        return ((obj == null) ? defaultValue : obj);
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@Nullable String msg, @NotNull Throwable t, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class, Throwable.class).newInstance(((msg == null) ? t.getMessage() : msg), t); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@NotNull Throwable t, @NotNull Class<T> throwableClass) {
        return wrapThrowable(null, t, throwableClass);
    }

    public static String tr(String str) {
        return ((str == null) ? null : str.trim());
    }

    public static String lc(String str) {
        return ((str == null) ? null : str.toLowerCase());
    }

    public static String uc(String str) {
        return ((str == null) ? null : str.toUpperCase());
    }

    public static boolean z(String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    public static boolean nz(String str) {
        return !z(str);
    }
}
