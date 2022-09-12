package com.projectgalen.lib.utils;

import com.projectgalen.lib.utils.delegates.DoIfNotNullReturning;
import com.projectgalen.lib.utils.delegates.DoIfNotNullVoid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Base64;

@SuppressWarnings("unused")
public class U {

    private U() {
    }

    public static @NotNull StringBuilder appendFormat(@NotNull StringBuilder sb, @NotNull String format, Object... args) {
        return sb.append(String.format(format, args));
    }

    public static @NotNull StringBuffer appendFormat(@NotNull StringBuffer sb, @NotNull String format, Object... args) {
        return sb.append(String.format(format, args));
    }

    public static byte @NotNull [] base64Decode(@NotNull String encStr) {
        return Base64.getDecoder().decode(encStr);
    }

    public static @NotNull String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static @NotNull String capitalize(@NotNull String str) {
        return (str.isBlank() ? str : String.format("%C%s", str.charAt(0), str.substring(1)));
    }

    public static @NotNull <T extends Throwable> T getThrowable(@NotNull String msg, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class).newInstance(msg); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull <T> T ifNull(@Nullable T obj, @NotNull T defaultValue) {
        return ((obj == null) ? defaultValue : obj);
    }

    public static String lc(String str) {
        return ((str == null) ? null : str.toLowerCase());
    }

    public static boolean nz(@Nullable String str) {
        return ((str != null) && (str.trim().length() > 0));
    }

    public static String tr(String str) {
        return ((str == null) ? null : str.trim());
    }

    public static String uc(String str) {
        return ((str == null) ? null : str.toUpperCase());
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@Nullable String msg, @NotNull Throwable t, @NotNull Class<T> throwableClass) {
        try { return throwableClass.getConstructor(String.class, Throwable.class).newInstance(((msg == null) ? t.getMessage() : msg), t); }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

    public static @NotNull <T extends Throwable> T wrapThrowable(@NotNull Throwable t, @NotNull Class<T> throwableClass) {
        return wrapThrowable(null, t, throwableClass);
    }

    public static boolean z(@Nullable String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    public static <P> void doIfNotNull(@Nullable P value, @NotNull DoIfNotNullVoid<P> delegate) {
        if(value != null) delegate.action(value);
    }

    public static <P, R> R getIfNotNull(@Nullable P value, @NotNull DoIfNotNullReturning<P, R> delegate) {
        return ((value == null) ? null : delegate.action(value));
    }

    public static @NotNull String join(char separator, @NotNull Object... args) {
        return join(Character.toString(separator), args);
    }

    public static @NotNull String join(@NotNull String separator, @NotNull Object... args) {
        if(args.length == 0) return "";
        StringBuilder sb = new StringBuilder().append(args[0]);
        for(int i = 1; i < args.length; i++) sb.append(separator).append(args[i]);
        return sb.toString();
    }
}
