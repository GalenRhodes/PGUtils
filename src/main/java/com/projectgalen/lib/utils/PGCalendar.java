package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGCalendar.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 07, 2023
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

import com.projectgalen.lib.utils.enums.Month;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Locale.Category;

@SuppressWarnings({ "unused" })
public class PGCalendar extends GregorianCalendar implements Cloneable {

    public PGCalendar(long millis) {
        this(millis, TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
    }

    public PGCalendar(long millis, @NotNull Locale aLocale) {
        this(millis, TimeZone.getDefault(), aLocale);
    }

    public PGCalendar(long millis, @NotNull TimeZone zone) {
        this(millis, zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    public PGCalendar(long millis, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        this(zone, aLocale);
        setTimeInMillis(millis);
    }

    public PGCalendar(@NotNull Calendar c) {
        this(c, c.getTimeZone(), Locale.getDefault(Locale.Category.FORMAT));
    }

    public PGCalendar(@NotNull Calendar c, @NotNull TimeZone zone) {
        this(c, zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    public PGCalendar(@NotNull Calendar c, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        this(zone, aLocale);
        setTime(c.getTime());
    }

    public PGCalendar(@NotNull Calendar c, @NotNull Locale aLocale) {
        this(c, c.getTimeZone(), aLocale);
    }

    public PGCalendar() {
        super();
    }

    public PGCalendar(TimeZone zone) {
        super(zone);
    }

    public PGCalendar(Locale aLocale) {
        super(aLocale);
    }

    public PGCalendar(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
    }

    public PGCalendar(int year, int month, int dayOfMonth) {
        super(year, month, dayOfMonth);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        super(year, month, dayOfMonth, hourOfDay, minute);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        super(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth) {
        super(year, month.getCalendarMonth(), dayOfMonth);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute) {
        super(year, month.getCalendarMonth(), dayOfMonth, hourOfDay, minute);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute, int second) {
        super(year, month.getCalendarMonth(), dayOfMonth, hourOfDay, minute, second);
    }

    public PGCalendar(int year, int month, int dayOfMonth, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public PGCalendar(int year, int month, int dayOfMonth, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public PGCalendar(int year, int month, int dayOfMonth, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public PGCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public PGCalendar(int year, @NotNull Month month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public PGCalendar(@NotNull Date dt, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        setTime(dt);
    }

    public PGCalendar(@NotNull Date dt) {
        super();
        setTime(dt);
    }

    public PGCalendar(@NotNull Date dt, @NotNull TimeZone zone) {
        super(zone);
        setTime(dt);
    }

    public PGCalendar(@NotNull Date dt, @NotNull Locale aLocale) {
        super(aLocale);
        setTime(dt);
    }

    public PGCalendar addAmPmHours(int value) {
        add(Calendar.HOUR, value);
        return this;
    }

    public PGCalendar addDays(int value) {
        add(Calendar.DATE, value);
        return this;
    }

    public PGCalendar addHours(int value) {
        add(Calendar.HOUR_OF_DAY, value);
        return this;
    }

    public PGCalendar addMilliseconds(int value) {
        add(Calendar.MILLISECOND, value);
        return this;
    }

    public PGCalendar addMinutes(int value) {
        add(Calendar.MINUTE, value);
        return this;
    }

    public PGCalendar addMonths(int value) {
        add(Calendar.MONTH, value);
        return this;
    }

    public PGCalendar addSeconds(int value) {
        add(Calendar.SECOND, value);
        return this;
    }

    public PGCalendar addYears(int value) {
        add(Calendar.YEAR, value);
        return this;
    }

    public @NotNull PGCalendar clearTime() {
        set(HOUR_OF_DAY, 0);
        set(MINUTE, 0);
        set(SECOND, 0);
        set(MILLISECOND, 0);
        return this;
    }

    @Override
    public PGCalendar clone() {
        return (PGCalendar)super.clone();
    }

    public @Range(from = 28, to = 31) int daysInMonth() {
        return Dates.daysInMonth(getRealMonth(), getYear());
    }

    public @Range(from = 0, to = 1) int getAmPm() {
        return get(Calendar.AM_PM);
    }

    public @Range(from = 0, to = 11) int getAmPmHour() {
        return get(Calendar.HOUR);
    }

    public @Range(from = 1, to = 31) int getDate() {
        return get(Calendar.DATE);
    }

    public @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int getDayOfWeek() {
        return get(Calendar.DAY_OF_WEEK);
    }

    public int getDstOffset() {
        return get(Calendar.DST_OFFSET);
    }

    public @NotNull Month getEnumMonth() {
        return Month.getMonth(getRealMonth());
    }

    public @Range(from = 0, to = 23) int getHour() {
        return get(Calendar.HOUR_OF_DAY);
    }

    public @Range(from = 0, to = 999) int getMillisecond() {
        return get(Calendar.MILLISECOND);
    }

    public @Range(from = 0, to = 59) int getMinute() {
        return get(Calendar.MINUTE);
    }

    public @Range(from = 0, to = 11) int getMonth() {
        return get(Calendar.MONTH);
    }

    public @Range(from = 1, to = 12) int getRealMonth() {
        return get(Calendar.MONTH) + 1;
    }

    public @Range(from = 0, to = 59) int getSecond() {
        return get(Calendar.SECOND);
    }

    public @NotNull java.sql.Date getSqlDate() {
        return Dates.toSQLDate(getTime());
    }

    public int getYear() {
        return get(Calendar.YEAR);
    }

    public boolean isInOpenRange(Date date1, Date date2) {
        return Dates.isInOpenRange(this.getTime(), date1, date2);
    }

    public boolean isInOpenRange(PGCalendar date1, PGCalendar date2) {
        return Dates.isInOpenRange(this, date1, date2);
    }

    public boolean isInRange(Date date1, Date date2) {
        return Dates.isInRange(this.getTime(), date1, date2);
    }

    public boolean isInRange(PGCalendar date1, PGCalendar date2) {
        return Dates.isInRange(this, date1, date2);
    }

    public boolean isLeapYear() {
        return isLeapYear(getYear());
    }

    public @NotNull PGCalendar setAll(int year, int month, int date) {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        return this;
    }

    public @NotNull PGCalendar setAll(int year, int month, int date, int hourOfDay, int minute, int second) {
        return setAll(year, month, date, hourOfDay, minute, second, 0);
    }

    public @NotNull PGCalendar setAll(int year, int month, int date, int hourOfDay, int minute, int second, int millisecond) {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
        set(MILLISECOND, millisecond);
        return this;
    }

    public @NotNull PGCalendar setAll(int year, @NotNull Month month, int date) {
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, date);
        return this;
    }

    public @NotNull PGCalendar setAll(int year, @NotNull Month month, int date, int hourOfDay, int minute, int second) {
        return setAll(year, month, date, hourOfDay, minute, second, 0);
    }

    public @NotNull PGCalendar setAll(int year, @NotNull Month month, int date, int hourOfDay, int minute, int second, int millisecond) {
        set(YEAR, year);
        set(MONTH, month.getCalendarMonth());
        set(DATE, date);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
        set(MILLISECOND, millisecond);
        return this;
    }

    public PGCalendar setAmPm(@MagicConstant(intValues = { Calendar.AM, Calendar.PM }) int value) {
        set(Calendar.AM_PM, value);
        return this;
    }

    public PGCalendar setAmPmHour(@Range(from = 0, to = 11) int value) {
        set(Calendar.HOUR, value);
        return this;
    }

    public PGCalendar setDate(@Range(from = 1, to = 31) int value) {
        set(Calendar.DATE, value);
        return this;
    }

    public PGCalendar setDayOfWeek(@Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int value) {
        set(Calendar.DAY_OF_WEEK, value);
        return this;
    }

    public PGCalendar setDstOffset(int value) {
        set(Calendar.DST_OFFSET, value);
        return this;
    }

    public @NotNull PGCalendar setFrom(@NotNull Calendar c) {
        setTimeZone(c.getTimeZone());
        set(YEAR, c.get(YEAR));
        set(MONTH, c.get(MONTH));
        set(DATE, c.get(DATE));
        set(HOUR_OF_DAY, c.get(HOUR_OF_DAY));
        set(MINUTE, c.get(MINUTE));
        set(SECOND, c.get(SECOND));
        set(MILLISECOND, c.get(MILLISECOND));
        return this;
    }

    public PGCalendar setHour(@Range(from = 0, to = 23) int value) {
        set(Calendar.HOUR_OF_DAY, value);
        return this;
    }

    public PGCalendar setMillisecond(@Range(from = 0, to = 999) int value) {
        set(Calendar.MILLISECOND, value);
        return this;
    }

    public PGCalendar setMinute(@Range(from = 0, to = 59) int value) {
        set(Calendar.MINUTE, value);
        return this;
    }

    public PGCalendar setMonth(@Range(from = 0, to = 11) int value) {
        set(Calendar.MONTH, value);
        return this;
    }

    public @NotNull PGCalendar setMonth(@NotNull Month em) {
        return setRealMonth(em.getId());
    }

    public PGCalendar setRealMonth(@Range(from = 1, to = 12) int value) {
        set(Calendar.MONTH, value - 1);
        return this;
    }

    public PGCalendar setSecond(@Range(from = 0, to = 59) int value) {
        set(Calendar.SECOND, value);
        return this;
    }

    public @NotNull PGCalendar setTime(int hourOfDay, int minute, int second) {
        return setTime(hourOfDay, minute, second, 0);
    }

    public @NotNull PGCalendar setTime(int hourOfDay, int minute, int second, int millisecond) {
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
        set(MILLISECOND, millisecond);
        return this;
    }

    public PGCalendar setYear(int value) {
        set(Calendar.YEAR, value);
        return this;
    }

    public java.sql.Date toSQLDate() {
        return new java.sql.Date(getTimeInMillis());
    }

    public Time toSQLTime() {
        return new Time(getTimeInMillis());
    }

    public Timestamp toSQLTimestamp() {
        return new Timestamp(getTimeInMillis());
    }

    @Override public String toString() {
        return new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss").format(getTime());
    }

    public static @Range(from = 28, to = 31) int daysInMonth(@Range(from = Calendar.JANUARY, to = Calendar.DECEMBER) int month, int year) {
        return Dates.daysInMonth(month + 1, year);
    }

    public static @Range(from = 28, to = 31) int daysInRealMonth(@Range(from = 1, to = 12) int realMonth, int year) {
        return Dates.daysInMonth(realMonth, year);
    }

    @Contract(" -> new")
    public static @NotNull PGCalendar distantFuture() {
        return new PGCalendar(Long.MAX_VALUE);
    }

    @Contract(" -> new")
    public static @NotNull PGCalendar distantPast() {
        return new PGCalendar(Long.MIN_VALUE);
    }

    @Contract(" -> new")
    public static @NotNull PGCalendar getInstance() {
        return new PGCalendar();
    }

    @Contract("null, _, _ -> null; !null, _, _ -> new")
    public static PGCalendar toCalendar(@Nullable Date dt, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        return ((dt == null) ? null : new PGCalendar(dt, zone, aLocale));
    }

    @Contract("null -> null; !null -> new")
    public static PGCalendar toCalendar(@Nullable Date dt) {
        return toCalendar(dt, TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
    }

    @Contract("null, _ -> null; !null, _ -> new")
    public static PGCalendar toCalendar(@Nullable Date dt, @NotNull Locale aLocale) {
        return toCalendar(dt, TimeZone.getDefault(), aLocale);
    }

    @Contract("null, _ -> null; !null, _ -> new")
    public static PGCalendar toCalendar(@Nullable Date dt, @NotNull TimeZone zone) {
        return toCalendar(dt, zone, Locale.getDefault(Category.FORMAT));
    }
}
