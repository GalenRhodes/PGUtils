package com.projectgalen.lib.utils.enums;

// ===========================================================================
//     PROJECT: PGBudgetDB
//    FILENAME: WeekDay.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 04, 2023
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

import com.projectgalen.lib.utils.PGResourceBundle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Calendar;

public enum WeekDay {

    SUNDAY(Calendar.SUNDAY), MONDAY(Calendar.MONDAY), TUESDAY(Calendar.TUESDAY), WEDNESDAY(Calendar.WEDNESDAY), THURSDAY(Calendar.THURSDAY), FRIDAY(Calendar.FRIDAY), SATURDAY(Calendar.SATURDAY);

    private static final PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");

    @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) private final int id;

    WeekDay(@Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int id) {
        this.id = id;
    }

    public int getDistanceFrom(@NotNull WeekDay wd) {
        return getDistance(wd.id, id);
    }

    public int getDistanceFrom(@Range(from = 1, to = 7) int wd) {
        return getDistance(wd, id);
    }

    public int getDistanceTo(@NotNull WeekDay wd) {
        return getDistance(id, wd.id);
    }

    public int getDistanceTo(@Range(from = 1, to = 7) int wd) {
        return getDistance(id, wd);
    }

    public @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int getId() {
        return id;
    }

    public @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int toCalendarWeekDay() {
        return id;
    }

    public @Override @NotNull String toString() {
        return switch(this) {/*@f0*/
            case SUNDAY    -> msgs.getString("enum.week_day.name.1");
            case MONDAY    -> msgs.getString("enum.week_day.name.2");
            case TUESDAY   -> msgs.getString("enum.week_day.name.3");
            case WEDNESDAY -> msgs.getString("enum.week_day.name.4");
            case THURSDAY  -> msgs.getString("enum.week_day.name.5");
            case FRIDAY    -> msgs.getString("enum.week_day.name.6");
            case SATURDAY  -> msgs.getString("enum.week_day.name.7");
        };/*@f1*/
    }

    public static int compare(@NotNull WeekDay wd1, @NotNull WeekDay wd2) {
        return (wd1.id - wd2.id);
    }

    public static int getDistance(@Range(from = 1, to = 7) int wdFrom, @Range(from = 1, to = 7) int wdTo) {
        return ((wdFrom <= wdTo) ? (wdTo - wdFrom) : (7 - (wdFrom - wdTo)));
    }

    @Contract(pure = true) public static int getDistance(@NotNull WeekDay wdFrom, @NotNull WeekDay wdTo) {
        return getDistance(wdFrom.id, wdTo.id);
    }

    public static @NotNull WeekDay getWeekDay(@Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int id) {
        return switch(id) {/*@f0*/
            case Calendar.SUNDAY    -> WeekDay.SUNDAY;
            case Calendar.MONDAY    -> WeekDay.MONDAY;
            case Calendar.TUESDAY   -> WeekDay.TUESDAY;
            case Calendar.WEDNESDAY -> WeekDay.WEDNESDAY;
            case Calendar.THURSDAY  -> WeekDay.THURSDAY;
            case Calendar.FRIDAY    -> WeekDay.FRIDAY;
            case Calendar.SATURDAY  -> WeekDay.SATURDAY;
            default -> throw new IllegalArgumentException(msgs.format("msg.err.invalid_enum_id", msgs.getString("msg.week_day"), id));
        };/*@f1*/
    }

    public static @NotNull WeekDay max(@NotNull WeekDay wd1, @NotNull WeekDay wd2) {
        return ((wd1.id >= wd2.id) ? wd1 : wd2);
    }

    public static @NotNull WeekDay min(@NotNull WeekDay wd1, @NotNull WeekDay wd2) {
        return ((wd1.id <= wd2.id) ? wd1 : wd2);
    }
}
