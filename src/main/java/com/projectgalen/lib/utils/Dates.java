package com.projectgalen.lib.utils;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Dates {
    private Dates() {
    }

    public static @NotNull Calendar createCalendar(int year, @MagicConstant(intValues = {
            Calendar.JANUARY,
            Calendar.FEBRUARY,
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
            Calendar.OCTOBER,
            Calendar.NOVEMBER,
            Calendar.DECEMBER,
            Calendar.UNDECIMBER
    }) int month, int date) {
        return createCalendar(year, month, date, TimeZone.getDefault(), Locale.getDefault());
    }

    @NotNull
    public static Calendar createCalendar(int year, @MagicConstant(intValues = {
            Calendar.JANUARY,
            Calendar.FEBRUARY,
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
            Calendar.OCTOBER,
            Calendar.NOVEMBER,
            Calendar.DECEMBER,
            Calendar.UNDECIMBER
    }) int month, int date, TimeZone tz, Locale locale) {
        return createCalendar(year, month, date, 0, 0, 0, 0, tz, locale);
    }

    public static @NotNull Calendar createCalendar(int year, @MagicConstant(intValues = {
            Calendar.JANUARY,
            Calendar.FEBRUARY,
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
            Calendar.OCTOBER,
            Calendar.NOVEMBER,
            Calendar.DECEMBER,
            Calendar.UNDECIMBER
    }) int month, int date, int hour24, int minute, int second, int ms) {
        return createCalendar(year, month, date, hour24, minute, second, ms, TimeZone.getDefault(), Locale.getDefault());
    }

    public static @NotNull Calendar createCalendar(int year, @MagicConstant(intValues = {
            Calendar.JANUARY,
            Calendar.FEBRUARY,
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
            Calendar.OCTOBER,
            Calendar.NOVEMBER,
            Calendar.DECEMBER,
            Calendar.UNDECIMBER
    }) int month, int date, int hour24, int minute, int second, int ms, @NotNull TimeZone tz, @NotNull Locale locale) {
        Calendar c = Calendar.getInstance(tz, locale);
        c.set(year, month, date, hour24, minute, second);
        c.set(Calendar.MILLISECOND, ms);
        return c;
    }

    public static @NotNull Timestamp getTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static @NotNull Calendar toCalendar(@NotNull Date dt, @NotNull TimeZone tz, @NotNull Locale locale) {
        Calendar c = Calendar.getInstance(tz, locale);
        c.setTime(dt);
        return c;
    }

    public static @NotNull Calendar toCalendar(@NotNull Date dt, @NotNull Locale locale) {
        return toCalendar(dt, TimeZone.getDefault(), locale);
    }

    public static @NotNull Calendar toCalendar(@NotNull Date dt, @NotNull TimeZone tz) {
        return toCalendar(dt, tz, Locale.getDefault());
    }

    public static @NotNull Calendar toCalendar(@NotNull Date dt) {
        return toCalendar(dt, TimeZone.getDefault(), Locale.getDefault());
    }

    public static @NotNull Date toDate(@NotNull Calendar c) {
        return new Date(c.getTimeInMillis());
    }
}
