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
import org.jetbrains.annotations.Range;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Dates {
    private static final PGResourceBundle              msgs       = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final Map<String, SimpleDateFormat> formatters = new TreeMap<>();

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

    /**
     * Determines the number of days in a month for the given month. If the month is February then the number of days will depend on if the current year is a leap year.
     *
     * @param month the month.
     * @return the number of days in the month.
     */
    public static @Range(from = 28, to = 31) int daysInMonth(@Range(from = 1, to = 12) int month) {
        return daysInMonth(month, Calendar.getInstance().get(Calendar.YEAR));
    }

    /**
     * Determines the number of days in a month for the given month. If the month is February then the number of days will depend on if the given year is a leap year.
     *
     * @param month the month.
     * @param year  the year that the month is in.
     * @return the number of days in the month.
     */
    public static @Range(from = 28, to = 31) int daysInMonth(@Range(from = 1, to = 12) int month, int year) {
        switch(month) {
            case 2:  return (isLeapYear(year) ? 29 : 28);/*@f0*/
            case 4:
            case 6:
            case 9:
            case 11: return 30;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: return 31;
            default: throw new IllegalArgumentException(String.format(msgs.getString("msg.err.month_out_of_range"), month));
        }/*@f1*/
    }

    public static @NotNull String format(@NotNull String pattern, @NotNull Calendar calendar) {
        return format(pattern, calendar.getTime());
    }

    public static @NotNull String format(@NotNull String pattern, @NotNull Date date) {
        synchronized(formatters) {
            SimpleDateFormat fmt = formatters.get(pattern);
            if(fmt == null) formatters.put(pattern, (fmt = new SimpleDateFormat(pattern)));
            return fmt.format(date);
        }
    }

    /**
     * Create and return a new instance of Calendar from the given milliseconds since the epoch.
     *
     * @param milliseconds The current date and time in milliseconds since the epoch.
     * @return A new instance of Calendar.
     */
    @Contract("_ -> new")
    public static @NotNull Calendar getCalendar(long milliseconds) {
        return getCalendar(milliseconds, null, null);
    }

    /**
     * Create and return a new instance of Calendar from the given milliseconds since the epoch.
     *
     * @param milliseconds The current date and time in milliseconds since the epoch.
     * @return A new instance of Calendar.
     */
    @Contract("_,_ -> new")
    public static @NotNull Calendar getCalendar(long milliseconds, @NotNull Locale locale) {
        return getCalendar(milliseconds, null, locale);
    }

    /**
     * Create and return a new instance of Calendar from the given milliseconds since the epoch.
     *
     * @param milliseconds The current date and time in milliseconds since the epoch.
     * @return A new instance of Calendar.
     */
    @Contract("_,_ -> new")
    public static @NotNull Calendar getCalendar(long milliseconds, @NotNull TimeZone tz) {
        return getCalendar(milliseconds, tz, null);
    }

    /**
     * Create and return a new instance of Calendar from the given milliseconds since the epoch.
     *
     * @param milliseconds The current date and time in milliseconds since the epoch.
     * @return A new instance of Calendar.
     */
    @Contract("_,_,_ -> new")
    public static @NotNull Calendar getCalendar(long milliseconds, @Nullable TimeZone tz, @Nullable Locale locale) {
        Calendar c = getCalendarInstance(tz, locale);
        c.setTimeInMillis(milliseconds);
        return c;
    }

    public static int @NotNull [] getDateComponents(@NotNull Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return new int[] {
                (c.get(Calendar.MONTH) + 1), c.get(Calendar.DATE), c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), c.get(Calendar.MILLISECOND)
        };
    }

    /**
     * Convienience method for <code>new GregorianCalendar().isLeapYear(year)</code>. Determines if the given year is a leap year. Returns true if the given year is a leap year. To specify BC year
     * numbers, 1 - year number must be given. For example, year BC 4 is specified as -3.
     *
     * @param year the given year.
     * @return <code>true</code> if the given year is a leap year; <code>false</code> otherwise.
     */
    public static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }

    @Contract("!null,_,_ -> new; null,_,_ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @Nullable TimeZone tz, @Nullable Locale locale) {
        if(dt == null) return null;
        Calendar c = getCalendarInstance(tz, locale);
        c.setTime(dt);
        return c;
    }

    @Contract("!null,_ -> new; null,_ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @NotNull Locale locale) {
        return toCalendar(dt, null, locale);
    }

    @Contract("!null,_ -> new; null,_ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @NotNull TimeZone tz) {
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

    @Contract("!null->new;null->null")
    public static java.sql.Date toSQLDate(Date dt) {
        return ((dt == null) ? null : new java.sql.Date(dt.getTime()));
    }

    @Contract("!null->new;null->null")
    public static java.sql.Date toSQLDate(Calendar c) {
        return ((c == null) ? null : new java.sql.Date(c.getTimeInMillis()));
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
