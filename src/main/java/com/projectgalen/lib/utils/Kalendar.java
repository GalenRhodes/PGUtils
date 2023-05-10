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

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.Locale.Category;

public class Kalendar extends GregorianCalendar implements Cloneable {

    public Kalendar(long millis) {
        this(millis, TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
    }

    public Kalendar(long millis, @NotNull Locale aLocale) {
        this(millis, TimeZone.getDefault(), aLocale);
    }

    public Kalendar(long millis, @NotNull TimeZone zone) {
        this(millis, zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    public Kalendar(long millis, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        this(zone, aLocale);
        setTimeInMillis(millis);
    }

    public Kalendar(@NotNull Calendar c) {
        this(c, c.getTimeZone(), Locale.getDefault(Locale.Category.FORMAT));
    }

    public Kalendar(@NotNull Calendar c, @NotNull TimeZone zone) {
        this(c, zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    public Kalendar(@NotNull Calendar c, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        this(zone, aLocale);
        setTime(c.getTime());
    }

    public Kalendar(@NotNull Calendar c, @NotNull Locale aLocale) {
        this(c, c.getTimeZone(), aLocale);
    }

    public Kalendar() {
        super();
    }

    public Kalendar(TimeZone zone) {
        super(zone);
    }

    public Kalendar(Locale aLocale) {
        super(aLocale);
    }

    public Kalendar(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
    }

    public Kalendar(int year, int month, int dayOfMonth) {
        super(year, month, dayOfMonth);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        super(year, month, dayOfMonth, hourOfDay, minute);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        super(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    public Kalendar(int year, int month, int dayOfMonth, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull TimeZone zone) {
        super(zone);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public Kalendar(int year, int month, int dayOfMonth, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull Locale aLocale) {
        super(aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public Kalendar(int year, int month, int dayOfMonth, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public Kalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, dayOfMonth);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public Kalendar(@NotNull Date dt, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        super(zone, aLocale);
        setTime(dt);
    }

    public Kalendar(@NotNull Date dt) {
        super();
        setTime(dt);
    }

    public Kalendar(@NotNull Date dt, @NotNull TimeZone zone) {
        super(zone);
        setTime(dt);
    }

    public Kalendar(@NotNull Date dt, @NotNull Locale aLocale) {
        super(aLocale);
        setTime(dt);
    }

    public Kalendar addAmPmHours(int value) {
        add(Calendar.HOUR, value);
        return this;
    }

    public Kalendar addDays(int value) {
        add(Calendar.DATE, value);
        return this;
    }

    public Kalendar addHours(int value) {
        add(Calendar.HOUR_OF_DAY, value);
        return this;
    }

    public Kalendar addMilliseconds(int value) {
        add(Calendar.MILLISECOND, value);
        return this;
    }

    public Kalendar addMinutes(int value) {
        add(Calendar.MINUTE, value);
        return this;
    }

    public Kalendar addMonths(int value) {
        add(Calendar.MONTH, value);
        return this;
    }

    public Kalendar addSeconds(int value) {
        add(Calendar.SECOND, value);
        return this;
    }

    public Kalendar addYears(int value) {
        add(Calendar.YEAR, value);
        return this;
    }

    @Override
    public Kalendar clone() {
        return (Kalendar)super.clone();
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

    public int getYear() {
        return get(Calendar.YEAR);
    }

    public boolean isInOpenRange(Kalendar date1, Kalendar date2) {
        return Dates.isInOpenRange(this, date1, date2);
    }

    public boolean isInOpenRange(Date date1, Date date2) {
        return Dates.isInOpenRange(this.getTime(), date1, date2);
    }

    public boolean isInRange(Date date1, Date date2) {
        return Dates.isInRange(this.getTime(), date1, date2);
    }

    public boolean isInRange(Kalendar date1, Kalendar date2) {
        return Dates.isInRange(this, date1, date2);
    }

    public boolean isLeapYear() {
        return isLeapYear(getYear());
    }

    public Kalendar setAmPm(@MagicConstant(intValues = { Calendar.AM, Calendar.PM }) int value) {
        set(Calendar.AM_PM, value);
        return this;
    }

    public Kalendar setAmPmHour(@Range(from = 0, to = 11) int value) {
        set(Calendar.HOUR, value);
        return this;
    }

    public Kalendar setDate(@Range(from = 1, to = 31) int value) {
        set(Calendar.DATE, value);
        return this;
    }

    public Kalendar setDayOfWeek(@Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int value) {
        set(Calendar.DAY_OF_WEEK, value);
        return this;
    }

    public Kalendar setDstOffset(int value) {
        set(Calendar.DST_OFFSET, value);
        return this;
    }

    public Kalendar setHour(@Range(from = 0, to = 23) int value) {
        set(Calendar.HOUR_OF_DAY, value);
        return this;
    }

    public Kalendar setMillisecond(@Range(from = 0, to = 999) int value) {
        set(Calendar.MILLISECOND, value);
        return this;
    }

    public Kalendar setMinute(@Range(from = 0, to = 59) int value) {
        set(Calendar.MINUTE, value);
        return this;
    }

    public Kalendar setMonth(@Range(from = 0, to = 11) int value) {
        set(Calendar.MONTH, value);
        return this;
    }

    public Kalendar setRealMonth(@Range(from = 1, to = 12) int value) {
        set(Calendar.MONTH, value - 1);
        return this;
    }

    public Kalendar setSecond(@Range(from = 0, to = 59) int value) {
        set(Calendar.SECOND, value);
        return this;
    }

    public Kalendar setYear(int value) {
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

    public static @Range(from = 28, to = 31) int daysInMonth(@Range(from = Calendar.JANUARY, to = Calendar.DECEMBER) int month, int year) {
        return Dates.daysInMonth(month + 1, year);
    }

    public static @Range(from = 28, to = 31) int daysInRealMonth(@Range(from = 1, to = 12) int realMonth, int year) {
        return Dates.daysInMonth(realMonth, year);
    }

    @Contract(" -> new")
    public static @NotNull Kalendar distantFuture() {
        return new Kalendar(Long.MAX_VALUE);
    }

    @Contract(" -> new")
    public static @NotNull Kalendar distantPast() {
        return new Kalendar(Long.MIN_VALUE);
    }

    @Contract("null, _ -> null; !null, _ -> new")
    public static Kalendar getInstance(@Nullable Date dt, @NotNull Locale aLocale) {
        return getInstance(dt, TimeZone.getDefault(), aLocale);
    }

    @Contract("null, _ -> null; !null, _ -> new")
    public static Kalendar getInstance(@Nullable Date dt, @NotNull TimeZone zone) {
        return getInstance(dt, zone, Locale.getDefault(Category.FORMAT));
    }

    @Contract("null, _, _ -> null; !null, _, _ -> new")
    public static Kalendar getInstance(@Nullable Date dt, @NotNull TimeZone zone, @NotNull Locale aLocale) {
        return ((dt == null) ? null : new Kalendar(dt, zone, aLocale));
    }

    @Contract(" -> new")
    public static @NotNull Kalendar getInstance() {
        return new Kalendar();
    }

    @Contract("null -> null; !null -> new")
    public static Kalendar getInstance(@Nullable Date dt) {
        return getInstance(dt, TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
    }
}
