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

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({ "DuplicatedCode" })
public final class Dates {
    private static final PGResourceBundle              msgs          = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final Map<String, SimpleDateFormat> formatters    = new TreeMap<>();
    private static final int[]                         DAYS_OF_MONTH = new int[]{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    private Dates() { }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addAmPm(@NotNull Calendar c, @MagicConstant(intValues = { Calendar.AM, Calendar.PM }) int value) {
        c.add(Calendar.AM_PM, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addAmPm(@NotNull Date c, @MagicConstant(intValues = { Calendar.AM, Calendar.PM }) int value) {
        return setAmPm(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addAmPmHour(@NotNull Calendar c, @Range(from = 0, to = 11) int value) {
        c.add(Calendar.HOUR, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addAmPmHour(@NotNull Date c, @Range(from = 0, to = 11) int value) {
        return setAmPmHour(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addDate(@NotNull Calendar c, @Range(from = 1, to = 31) int value) {
        c.add(Calendar.DATE, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addDate(@NotNull Date c, @Range(from = 1, to = 31) int value) {
        return setDate(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addDayOfWeek(@NotNull Calendar c, @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int value) {
        c.add(Calendar.DAY_OF_WEEK, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addDayOfWeek(@NotNull Date c, @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int value) {
        return setDayOfWeek(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addDstOffset(@NotNull Calendar c, int value) {
        c.add(Calendar.DST_OFFSET, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addDstOffset(@NotNull Date c, int value) {
        return setDstOffset(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addHour(@NotNull Calendar c, @Range(from = 0, to = 23) int value) {
        c.add(Calendar.HOUR_OF_DAY, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addHour(@NotNull Date c, @Range(from = 0, to = 23) int value) {
        return setHour(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addMillisecond(@NotNull Calendar c, @Range(from = 0, to = 999) int value) {
        c.add(Calendar.MILLISECOND, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addMillisecond(@NotNull Date c, @Range(from = 0, to = 999) int value) {
        return setMillisecond(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addMinute(@NotNull Calendar c, @Range(from = 0, to = 59) int value) {
        c.add(Calendar.MINUTE, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addMinute(@NotNull Date c, @Range(from = 0, to = 59) int value) {
        return setMinute(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addMonth(@NotNull Calendar c, @Range(from = 0, to = 11) int value) {
        c.add(Calendar.MONTH, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addMonth(@NotNull Date c, @Range(from = 0, to = 11) int value) {
        return setMonth(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addRealMonth(@NotNull Calendar c, @Range(from = 1, to = 12) int value) {
        c.add(Calendar.MONTH, value - 1);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addRealMonth(@NotNull Date c, @Range(from = 1, to = 12) int value) {
        return setRealMonth(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addSecond(@NotNull Calendar c, @Range(from = 0, to = 59) int value) {
        c.add(Calendar.SECOND, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addSecond(@NotNull Date c, @Range(from = 0, to = 59) int value) {
        return setSecond(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar addYear(@NotNull Calendar c, int value) {
        c.add(Calendar.YEAR, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date addYear(@NotNull Date c, int value) {
        return setYear(toCalendar(c), value).getTime();
    }

    @Contract("_ -> new")
    public static @NotNull Calendar copyOf(@NotNull Calendar c) {
        Calendar copy = Calendar.getInstance();
        copy.setTimeInMillis(c.getTimeInMillis());
        return copy;
    }

    @Contract("_ -> new")
    public static @NotNull Date copyOf(@NotNull Date date) {
        return new Date(date.getTime());
    }

    @Contract("_,_,_ -> new")
    public static @NotNull Calendar createCalendar(int year, @MagicConstant(intValues = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER, Calendar.UNDECIMBER }) int month, int date) {/*@f0*/
        return createCalendar(year, month, date, null, null);/*@f1*/
    }

    @Contract("_,_,_,_,_ -> new")
    public static @NotNull Calendar createCalendar(int year, @MagicConstant(intValues = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER, Calendar.UNDECIMBER }) int month, int date, @Nullable TimeZone tz, @Nullable Locale locale) {/*@f0*/
        return createCalendar(year, month, date, 0, 0, 0, 0, tz, locale);/*@f1*/
    }

    @Contract("_,_,_,_,_,_,_ -> new")
    public static @NotNull Calendar createCalendar(int year, @MagicConstant(intValues = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER, Calendar.UNDECIMBER }) int month, int date, int hour24, int minute, int second, int ms) {/*@f0*/
        return createCalendar(year, month, date, hour24, minute, second, ms, null, null);/*@f1*/
    }

    @Contract("_,_,_,_,_,_,_,_,_ -> new")
    public static @NotNull Calendar createCalendar(int year, @MagicConstant(intValues = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER, Calendar.UNDECIMBER }) int month, int date, int hour24, int minute, int second, int ms, @Nullable TimeZone tz, @Nullable Locale locale) {/*@f0*/
        Calendar c = getCalendarInstance(tz, locale);/*@f1*/
        c.set(year, month, date, hour24, minute, second);
        c.set(Calendar.MILLISECOND, ms);
        return c;
    }

    public static boolean datesOverlap(Date dateStart1, Date dateEnd1, Date dateStart2, Date dateEnd2) {
        return datesOverlap(PGCalendar.toCalendar(dateStart1), PGCalendar.toCalendar(dateEnd1), PGCalendar.toCalendar(dateStart2), PGCalendar.toCalendar(dateEnd2));
    }

    public static boolean datesOverlap(Calendar startDate1, Calendar endDate1, Calendar startDate2, Calendar endDate2) {
        Calendar start1 = ((startDate1 == null) ? PGCalendar.distantPast() : startDate1);
        Calendar start2 = ((startDate2 == null) ? PGCalendar.distantPast() : startDate2);
        Calendar end1   = ((endDate1 == null) ? PGCalendar.distantFuture() : endDate1);
        Calendar end2   = ((endDate2 == null) ? PGCalendar.distantFuture() : endDate2);

        if(end1.before(start1)) return datesOverlap(end1, start1, start2, end2);
        if(end2.after(start2)) return datesOverlap(start1, end1, end2, start2);
        return !((end2.compareTo(start1) <= 0) || (end1.compareTo(start2) <= 0));
    }

    public static @Range(from = 28, to = 31) int daysInMonth(@NotNull Calendar c) {
        return Dates.daysInMonth(getRealMonth(c), getYear(c));
    }

    /**
     * Determines the number of days in a month for the given month. If the month is February then the number of days will depend on if the current year is a leap year.
     *
     * @param month the month.
     *
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
     *
     * @return the number of days in the month.
     */
    public static @Range(from = 28, to = 31) int daysInMonth(@Range(from = 1, to = 12) int month, int year) {
        return (DAYS_OF_MONTH[month - 1] + (((month == 2) && isLeapYear(year)) ? 1 : 0));
    }

    public static @NotNull Calendar distantFuture() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.MAX_VALUE);
        return c;
    }

    public static @NotNull Calendar distantPast() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.MIN_VALUE);
        return c;
    }

    public static @NotNull String format(@NotNull String pattern, @Nullable Calendar calendar) {
        return format(pattern, calendar, "");
    }

    public static @NotNull String format(@NotNull String pattern, @Nullable Calendar calendar, @NotNull String defaultString) {
        return ((calendar == null) ? defaultString : format(pattern, calendar.getTime()));
    }

    public static @NotNull String format(@NotNull String pattern, @Nullable Date date) {
        return format(pattern, date, "");
    }

    public static @NotNull String format(@NotNull String pattern, @Nullable Date date, @NotNull String defaultString) {
        if(date == null) return defaultString;
        synchronized(formatters) {
            SimpleDateFormat fmt = formatters.get(pattern);
            if(fmt == null) formatters.put(pattern, (fmt = new SimpleDateFormat(pattern)));
            return fmt.format(date);
        }
    }

    public static @Range(from = 0, to = 1) int getAmPm(@NotNull Calendar c) {
        return c.get(Calendar.AM_PM);
    }

    public static @Range(from = 0, to = 11) int getAmPmHour(@NotNull Calendar c) {
        return c.get(Calendar.HOUR);
    }

    /**
     * Create and return a new instance of Calendar from the given milliseconds since the epoch.
     *
     * @param milliseconds The current date and time in milliseconds since the epoch.
     *
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
     *
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
     *
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
     *
     * @return A new instance of Calendar.
     */
    @Contract("_,_,_ -> new")
    public static @NotNull Calendar getCalendar(long milliseconds, @Nullable TimeZone tz, @Nullable Locale locale) {
        Calendar c = getCalendarInstance(tz, locale);
        c.setTimeInMillis(milliseconds);
        return c;
    }

    public static @NotNull java.sql.Date getCurrentSQLDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    public static @NotNull Time getCurrentSQLTime() {
        return new Time(System.currentTimeMillis());
    }

    public static @NotNull Timestamp getCurrentSQLTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static @Range(from = 1, to = 31) int getDate(@NotNull Calendar c) {
        return c.get(Calendar.DATE);
    }

    public static int @NotNull [] getDateComponents(@NotNull Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return new int[]{ c.get(Calendar.MONTH) + 1,
                          c.get(Calendar.DATE),
                          c.get(Calendar.YEAR),
                          c.get(Calendar.HOUR_OF_DAY),
                          c.get(Calendar.MINUTE),
                          c.get(Calendar.SECOND),
                          c.get(Calendar.MILLISECOND) };
    }

    public static @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int getDayOfWeek(@NotNull Calendar c) {
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDstOffset(@NotNull Calendar c) {
        return c.get(Calendar.DST_OFFSET);
    }

    public static @Range(from = 0, to = 23) int getHour(@NotNull Calendar c) {
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static @Range(from = 0, to = 999) int getMillisecond(@NotNull Calendar c) {
        return c.get(Calendar.MILLISECOND);
    }

    public static @Range(from = 0, to = 59) int getMinute(@NotNull Calendar c) {
        return c.get(Calendar.MINUTE);
    }

    public static @Range(from = 0, to = 11) int getMonth(@NotNull Calendar c) {
        return c.get(Calendar.MONTH);
    }

    public static @Range(from = 1, to = 12) int getRealMonth(@NotNull Calendar c) {
        return c.get(Calendar.MONTH) + 1;
    }

    public static @Range(from = 0, to = 59) int getSecond(@NotNull Calendar c) {
        return c.get(Calendar.SECOND);
    }

    @Contract("-> new")
    public static @NotNull Timestamp getTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static int getYear(@NotNull Calendar c) {
        return c.get(Calendar.YEAR);
    }

    public static boolean isInOpenRange(@NotNull Calendar when, Calendar date1, Calendar date2) {
        if(date1 == null) date1 = distantPast();
        if(date2 == null) date2 = distantFuture();
        if(date2.before(date1)) return isInOpenRange(when, date2, date1);
        return ((when.compareTo(date1) >= 0) && (when.compareTo(date2) < 0));
    }

    public static boolean isInOpenRange(@NotNull Date when, Date date1, Date date2) {
        if(date1 == null) date1 = distantPast().getTime();
        if(date2 == null) date2 = distantFuture().getTime();
        if(date2.before(date1)) return isInOpenRange(when, date2, date1);
        return ((when.compareTo(date1) >= 0) && (when.compareTo(date2) < 0));
    }

    public static boolean isInRange(@NotNull Date when, Date date1, Date date2) {
        if(date1 == null) date1 = distantPast().getTime();
        if(date2 == null) date2 = distantFuture().getTime();
        if(date2.before(date1)) return isInRange(when, date2, date1);
        return ((when.compareTo(date1) >= 0) && (when.compareTo(date2) <= 0));
    }

    public static boolean isInRange(@NotNull Calendar when, Calendar date1, Calendar date2) {
        if(date1 == null) date1 = distantPast();
        if(date2 == null) date2 = distantFuture();
        if(date2.before(date1)) return isInRange(when, date2, date1);
        return ((when.compareTo(date1) >= 0) && (when.compareTo(date2) <= 0));
    }

    public static boolean isLeapYear(@NotNull Calendar c) {
        return isLeapYear(getYear(c));
    }

    /**
     * Convienience method for <code>new GregorianCalendar().isLeapYear(year)</code>. Determines if the given year is a leap year. Returns true if the given year is a leap year. To specify BC year
     * numbers, 1 - year number must be given. For example, year BC 4 is specified as -3.
     *
     * @param year the given year.
     *
     * @return <code>true</code> if the given year is a leap year; <code>false</code> otherwise.
     */
    public static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }

    @Contract(pure = true, value = "null, null -> fail")
    public static <T extends Calendar> @NotNull T max(@Nullable T c1, @Nullable T c2) {
        if((c1 == null) && (c2 == null)) throw new NullPointerException(msgs.getString("msg.err.both_dates_null"));
        if(c1 == null) return c2;
        if(c2 == null) return c1;
        return ((c1.compareTo(c2) >= 0) ? c1 : c2);
    }

    @Contract(pure = true, value = "null, null -> fail")
    public static <T extends Date> @NotNull T max(@Nullable T c1, @Nullable T c2) {
        if((c1 == null) && (c2 == null)) throw new NullPointerException(msgs.getString("msg.err.both_dates_null"));
        if(c1 == null) return c2;
        if(c2 == null) return c1;
        return ((c1.compareTo(c2) >= 0) ? c1 : c2);
    }

    @Contract(pure = true, value = "null, null -> fail")
    public static <T extends Calendar> @NotNull T min(@Nullable T c1, @Nullable T c2) {
        if((c1 == null) && (c2 == null)) throw new NullPointerException(msgs.getString("msg.err.both_dates_null"));
        if(c1 == null) return c2;
        if(c2 == null) return c1;
        return ((c1.compareTo(c2) <= 0) ? c1 : c2);
    }

    @Contract(pure = true, value = "null, null -> fail")
    public static <T extends Date> @NotNull T min(@Nullable T c1, @Nullable T c2) {
        if((c1 == null) && (c2 == null)) throw new NullPointerException(msgs.getString("msg.err.both_dates_null"));
        if(c1 == null) return c2;
        if(c2 == null) return c1;
        return ((c1.compareTo(c2) <= 0) ? c1 : c2);
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setAmPm(@NotNull Calendar c, @MagicConstant(intValues = { Calendar.AM, Calendar.PM }) int value) {
        c.set(Calendar.AM_PM, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setAmPm(@NotNull Date c, @MagicConstant(intValues = { Calendar.AM, Calendar.PM }) int value) {
        return setAmPm(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setAmPmHour(@NotNull Calendar c, @Range(from = 0, to = 11) int value) {
        c.set(Calendar.HOUR, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setAmPmHour(@NotNull Date c, @Range(from = 0, to = 11) int value) {
        return setAmPmHour(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setDate(@NotNull Calendar c, @Range(from = 1, to = 31) int value) {
        c.set(Calendar.DATE, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setDate(@NotNull Date c, @Range(from = 1, to = 31) int value) {
        return setDate(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setDayOfWeek(@NotNull Calendar c, @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int value) {
        c.set(Calendar.DAY_OF_WEEK, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setDayOfWeek(@NotNull Date c, @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int value) {
        return setDayOfWeek(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setDstOffset(@NotNull Calendar c, int value) {
        c.set(Calendar.DST_OFFSET, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setDstOffset(@NotNull Date c, int value) {
        return setDstOffset(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setHour(@NotNull Calendar c, @Range(from = 0, to = 23) int value) {
        c.set(Calendar.HOUR_OF_DAY, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setHour(@NotNull Date c, @Range(from = 0, to = 23) int value) {
        return setHour(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setMillisecond(@NotNull Calendar c, @Range(from = 0, to = 999) int value) {
        c.set(Calendar.MILLISECOND, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setMillisecond(@NotNull Date c, @Range(from = 0, to = 999) int value) {
        return setMillisecond(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setMinute(@NotNull Calendar c, @Range(from = 0, to = 59) int value) {
        c.set(Calendar.MINUTE, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setMinute(@NotNull Date c, @Range(from = 0, to = 59) int value) {
        return setMinute(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setMonth(@NotNull Calendar c, @Range(from = 0, to = 11) int value) {
        c.set(Calendar.MONTH, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setMonth(@NotNull Date c, @Range(from = 0, to = 11) int value) {
        return setMonth(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setRealMonth(@NotNull Calendar c, @Range(from = 1, to = 12) int value) {
        c.set(Calendar.MONTH, value - 1);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setRealMonth(@NotNull Date c, @Range(from = 1, to = 12) int value) {
        return setRealMonth(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setSecond(@NotNull Calendar c, @Range(from = 0, to = 59) int value) {
        c.set(Calendar.SECOND, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setSecond(@NotNull Date c, @Range(from = 0, to = 59) int value) {
        return setSecond(toCalendar(c), value).getTime();
    }

    @Contract("_, _ -> param1")
    public static @NotNull Calendar setYear(@NotNull Calendar c, int value) {
        c.set(Calendar.YEAR, value);
        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull Date setYear(@NotNull Date c, int value) {
        return setYear(toCalendar(c), value).getTime();
    }

    @Contract("!null,_,_ -> new; null,_,_ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @Nullable TimeZone tz, @Nullable Locale locale) {
        if(dt == null) return null;
        Calendar c = getCalendarInstance(tz, locale);
        c.setTime(dt);
        return c;
    }

    @Contract("!null, _ -> new; null, _ -> null")
    public static Calendar toCalendar(@Nullable Date dt, @NotNull Locale locale) {
        return toCalendar(dt, null, locale);
    }

    @Contract("!null, _ -> new; null, _ -> null")
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

    @Contract("!null -> new; null -> null")
    public static java.sql.Date toSQLDate(Date dt) {
        return ((dt instanceof java.sql.Date) ? ((java.sql.Date)dt) : ((dt == null) ? null : new java.sql.Date(dt.getTime())));
    }

    @Contract("!null -> new; null -> null")
    public static java.sql.Date toSQLDate(Calendar c) {
        return ((c == null) ? null : new java.sql.Date(c.getTimeInMillis()));
    }

    @Contract("null -> null; !null -> new")
    public static Timestamp toTimestamp(@Nullable Calendar cal) {
        return ((cal == null) ? null : new Timestamp(cal.getTimeInMillis()));
    }

    @Contract("null -> null; !null -> new")
    public static Timestamp toTimestamp(@Nullable Date dt) {
        return ((dt instanceof Timestamp) ? ((Timestamp)dt) : ((dt == null) ? null : new Timestamp(dt.getTime())));
    }

    @Contract("_, _ -> new")
    private static @NotNull Calendar getCalendarInstance(@Nullable TimeZone tz, @Nullable Locale locale) {
        return Calendar.getInstance(((tz == null) ? TimeZone.getDefault() : tz), ((locale == null) ? Locale.getDefault() : locale));
    }

    public static int getDate(@NotNull Date date) {
        return PGCalendar.toCalendar(date).getDate();
    }

    public static int getDayOfWeek(@NotNull Date date) {
        return PGCalendar.toCalendar(date).getDayOfWeek();
    }

    public static int getHours(@NotNull Date date) {
        return PGCalendar.toCalendar(date).getHour();
    }

    public static int getMinutes(@NotNull Date date) {
        return PGCalendar.toCalendar(date).getMinute();
    }

    public static int getMonth(@NotNull Date date) {
        return PGCalendar.toCalendar(date).getMonth();
    }

    public static int getRealMonth(@NotNull Date date) {
        return (getMonth(date) + 1);
    }

    public static int getSeconds(@NotNull Date date) {
        return PGCalendar.toCalendar(date).getSecond();
    }

    public static int getYear(@NotNull Date date) {
        return PGCalendar.toCalendar(date).getYear();
    }
}
