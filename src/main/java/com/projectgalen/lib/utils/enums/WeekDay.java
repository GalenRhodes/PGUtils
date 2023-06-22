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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Calendar;

public enum WeekDay {

    SUNDAY(Calendar.SUNDAY), MONDAY(Calendar.MONDAY), TUESDAY(Calendar.TUESDAY), WEDNESDAY(Calendar.WEDNESDAY), THURSDAY(Calendar.THURSDAY), FRIDAY(Calendar.FRIDAY), SATURDAY(Calendar.SATURDAY);

    @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) private final int id;

    WeekDay(@Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int id) {
        this.id = id;
    }

    public @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int getId() {
        return id;
    }

    public @Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int toCalendarWeekDay() {
        return id;
    }

    public static @NotNull WeekDay max(@NotNull WeekDay wd1, @NotNull WeekDay wd2) {
        return ((wd1.id >= wd2.id) ? wd1 : wd2);
    }

    public static @NotNull WeekDay min(@NotNull WeekDay wd1, @NotNull WeekDay wd2) {
        return ((wd1.id <= wd2.id) ? wd1 : wd2);
    }

    @Override
    public @NotNull String toString() {
        PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
        return msgs.getString(String.format("enum.week_day.name.%d", id), super.toString());
    }

    public static @NotNull WeekDay getWeekDay(@Range(from = Calendar.SUNDAY, to = Calendar.SATURDAY) int id) {
        for(WeekDay e : WeekDay.values()) if(e.id == id) return e;
        PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
        throw new IllegalArgumentException(msgs.format("msg.err.invalid_enum_id", msgs.getString("msg.week_day"), id));
    }
}
