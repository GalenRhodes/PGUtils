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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class Dates {
    private Dates() { }

    @Contract("-> new")
    public static @NotNull Timestamp getTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    @Contract("_,_,_ -> new")
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
        return createCalendar(year, month, date, null, null);
    }

    @Contract("_,_,_,_,_ -> new")
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
    }) int month, int date, @Nullable TimeZone tz, @Nullable Locale locale) {
        return createCalendar(year, month, date, 0, 0, 0, 0, tz, locale);
    }

    @Contract("_,_,_,_,_,_,_ -> new")
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
        return createCalendar(year, month, date, hour24, minute, second, ms, null, null);
    }

    @Contract("_,_,_,_,_,_,_,_,_ -> new")
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
    }) int month, int date, int hour24, int minute, int second, int ms, @Nullable TimeZone tz, @Nullable Locale locale) {
        Calendar c = getCalendarInstance(tz, locale);
        c.set(year, month, date, hour24, minute, second);
        c.set(Calendar.MILLISECOND, ms);
        return c;
    }

    @Contract("!null,_,_ -> new; null,_,_ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @Nullable TimeZone tz, @Nullable Locale locale) {
        if(dt == null) return null;
        Calendar c = getCalendarInstance(tz, locale);
        c.setTime(dt);
        return c;
    }

    @Contract("!null,_ -> new; null,_ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @Nullable Locale locale) {
        return toCalendar(dt, null, locale);
    }

    @Contract("!null,_ -> new; null,_ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @Nullable TimeZone tz) {
        return toCalendar(dt, tz, null);
    }

    @Contract("!null -> new; null -> null")
    public static Calendar toCalendar(@Nullable Date dt) {
        return toCalendar(dt, null, null);
    }

    @Contract("!null -> new; null -> null")
    public static Date toDate(@Nullable Calendar c) {
        return ((c == null) ? null : new Date(c.getTimeInMillis()));
    }

    @Contract("null -> null; !null -> new")
    public static Timestamp toTimestamp(@Nullable Calendar cal) {
        return ((cal == null) ? null : new Timestamp(cal.getTimeInMillis()));
    }

    @Contract("_,_ -> new")
    private static @NotNull Calendar getCalendarInstance(@Nullable TimeZone tz, @Nullable Locale locale) {
        return Calendar.getInstance(((tz == null) ? TimeZone.getDefault() : tz), ((locale == null) ? Locale.getDefault() : locale));
    }
}
