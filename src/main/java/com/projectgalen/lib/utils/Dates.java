package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Dates.java
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

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class Dates {
    private Dates() { }

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
    }) int month, int date, int hour24, int minute, int second, int ms) {
        return createCalendar(year, month, date, hour24, minute, second, ms, TimeZone.getDefault(), Locale.getDefault());
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
    }) int month, int date, int hour24, int minute, int second, int ms, @NotNull TimeZone tz, @NotNull Locale locale) {
        Calendar c = Calendar.getInstance(tz, locale);
        c.set(year, month, date, hour24, minute, second);
        c.set(Calendar.MILLISECOND, ms);
        return c;
    }

    @NotNull
    public static Timestamp getTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    @NotNull
    public static Calendar toCalendar(@NotNull Date dt, @NotNull TimeZone tz, @NotNull Locale locale) {
        Calendar c = Calendar.getInstance(tz, locale);
        c.setTime(dt);
        return c;
    }

    @NotNull
    public static Calendar toCalendar(@NotNull Date dt, @NotNull Locale locale) {
        return toCalendar(dt, TimeZone.getDefault(), locale);
    }

    @NotNull
    public static Calendar toCalendar(@NotNull Date dt, @NotNull TimeZone tz) {
        return toCalendar(dt, tz, Locale.getDefault());
    }

    @NotNull
    public static Calendar toCalendar(@NotNull Date dt) {
        return toCalendar(dt, TimeZone.getDefault(), Locale.getDefault());
    }

    @NotNull
    public static Date toDate(@NotNull Calendar c) {
        return new Date(c.getTimeInMillis());
    }
}
