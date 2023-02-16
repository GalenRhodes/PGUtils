package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGMath.java
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class PGMath {
    private PGMath() { }

    @Contract("null -> null; !null -> !null")
    public static BigDecimal getBigDecimal(@Nullable Number num) {
        return ((num == null) ? null : ((num instanceof BigDecimal) ? (BigDecimal)num : ((num instanceof BigInteger) ? new BigDecimal((BigInteger)num) : BigDecimal.valueOf(num.doubleValue()))));
    }

    @Contract("null -> null; !null -> !null")
    public static BigInteger getBigInteger(@Nullable Number num) {
        return ((num == null) ? null : ((num instanceof BigInteger) ? (BigInteger)num : ((num instanceof BigDecimal) ? ((BigDecimal)num).toBigInteger() : BigInteger.valueOf(num.longValue()))));
    }
}
