package com.projectgalen.lib.utils.enums;

// ===========================================================================
//     PROJECT: PGBudgetDB
//    FILENAME: Month.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: February 13, 2023
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
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Calendar;

public enum Month {

    JANUARY(1), FEBRUARY(2), MARCH(3), APRIL(4), MAY(5), JUNE(6), JULY(7), AUGUST(8), SEPTEMBER(9), OCTOBER(10), NOVEMBER(11), DECEMBER(12);

    @Range(from = 1, to = 12) private final int id;

    Month(@Range(from = 1, to = 12) int id) {
        this.id = id;
    }

    public @MagicConstant(intValues = { Calendar.JANUARY,
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
                                        Calendar.DECEMBER }) int getCalendarMonth() {
        //noinspection MagicConstant
        return (id - 1);
    }

    public @Range(from = 1, to = 12) int getId() {
        return id;
    }

    public @Override @NotNull String toString() {
        PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
        return msgs.getString(String.format("enum.month.name.%d", id), super.toString());
    }

    public static int compare(@NotNull Month m1, @NotNull Month m2) {
        return (m1.id - m2.id);
    }

    public static @NotNull Month getMonth(@Range(from = 1, to = 12) int id) {
        for(Month e : Month.values()) if(e.id == id) return e;
        PGResourceBundle msgs = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
        throw new IllegalArgumentException(msgs.format("msg.err.invalid_enum_id", msgs.getString("text.month"), id));
    }

    public static @NotNull Month max(@NotNull Month m1, @NotNull Month m2) {
        return ((m1.id >= m2.id) ? m1 : m2);
    }

    public static @NotNull Month min(@NotNull Month m1, @NotNull Month m2) {
        return ((m1.id <= m2.id) ? m1 : m2);
    }
}
