package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PGMath {
    private PGMath() {
    }

    public static @NotNull BigDecimal getBigDecimal(@NotNull Number num) {
        return ((num instanceof BigDecimal) ? (BigDecimal)num : ((num instanceof BigInteger) ? new BigDecimal((BigInteger)num) : BigDecimal.valueOf(num.doubleValue())));
    }

    public static @NotNull BigInteger getBigInteger(@NotNull Number num) {
        return ((num instanceof BigInteger) ? (BigInteger)num : ((num instanceof BigDecimal) ? ((BigDecimal)num).toBigInteger() : BigInteger.valueOf(num.longValue())));
    }
}
