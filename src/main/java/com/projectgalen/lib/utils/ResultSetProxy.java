package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: ResultSetProxy.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: June 13, 2023
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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;

@SuppressWarnings({ "unused", "FieldCanBeLocal", "MismatchedReadAndWriteOfArray" })
public class ResultSetProxy {

    private final ResultSetMetaDataProxy resultSetMetaData;
    private final Object[]               data;

    public ResultSetProxy(@NotNull ResultSet rs, @NotNull ResultSetMetaDataProxy rsmd) throws SQLException {
        super();
        resultSetMetaData = rsmd;
        data              = new Object[resultSetMetaData.columnCount];
        for(int i = 0; i < data.length; i++) data[i] = rs.getObject(i + 1);
    }

    public int findColumn(String columnLabel) {
        if(U.nz(columnLabel)) {
            for(int i = 0; i < resultSetMetaData.columnCount; i++) if(Objects.equals(resultSetMetaData.columnLabel[i], columnLabel)) return (i + 1);
            for(int i = 0; i < resultSetMetaData.columnCount; i++) if(Objects.equals(resultSetMetaData.columnName[i], columnLabel)) return (i + 1);
        }
        throw new IllegalArgumentException(String.format("Columns \"%s\" not found.", columnLabel));
    }

    public java.io.InputStream getAsciiStream(int columnIndex) {
        return null;
    }

    public java.io.InputStream getAsciiStream(String columnLabel) {
        return getAsciiStream(findColumn(columnLabel));
    }

    public BigDecimal getBigDecimal(int columnIndex) {
        return BigDecimal.ZERO;
    }

    public BigDecimal getBigDecimal(String columnLabel) {
        return getBigDecimal(findColumn(columnLabel));
    }

    public java.io.InputStream getBinaryStream(int columnIndex) {
        return null;
    }

    public java.io.InputStream getBinaryStream(String columnLabel) {
        return getBinaryStream(findColumn(columnLabel));
    }

    public boolean getBoolean(int columnIndex) {
        return false;
    }

    public boolean getBoolean(String columnLabel) {
        return getBoolean(findColumn(columnLabel));
    }

    public byte getByte(int columnIndex) {
        return (byte)0;
    }

    public byte getByte(String columnLabel) {
        return getByte(findColumn(columnLabel));
    }

    public byte[] getBytes(int columnIndex) {
        return new byte[0];
    }

    public byte[] getBytes(String columnLabel) {
        return getBytes(findColumn(columnLabel));
    }

    public java.io.Reader getCharacterStream(int columnIndex) {
        return null;
    }

    public java.io.Reader getCharacterStream(String columnLabel) {
        return getCharacterStream(findColumn(columnLabel));
    }

    public java.sql.Date getDate(int columnIndex) {
        return null;
    }

    public java.sql.Date getDate(String columnLabel) {
        return getDate(findColumn(columnLabel));
    }

    public double getDouble(int columnIndex) {
        return 0;
    }

    public double getDouble(String columnLabel) {
        return getDouble(findColumn(columnLabel));
    }

    public float getFloat(int columnIndex) {
        return 0;
    }

    public float getFloat(String columnLabel) {
        return getFloat(findColumn(columnLabel));
    }

    public int getInt(int columnIndex) {
        return 0;
    }

    public int getInt(String columnLabel) {
        return getInt(findColumn(columnLabel));
    }

    public long getLong(int columnIndex) {
        return 0;
    }

    public long getLong(String columnLabel) {
        return getLong(findColumn(columnLabel));
    }

    public ResultSetMetaDataProxy getMetaData() {
        return resultSetMetaData;
    }

    public Object getObject(int columnIndex) {
        return null;
    }

    public Object getObject(String columnLabel) {
        return getObject(findColumn(columnLabel));
    }

    public ResultSetMetaDataProxy getResultSetMetaData() {
        return resultSetMetaData;
    }

    public int getRow() {
        return 0;
    }

    public short getShort(int columnIndex) {
        return 0;
    }

    public short getShort(String columnLabel) {
        return getShort(findColumn(columnLabel));
    }

    public String getString(int columnIndex) {
        return null;
    }

    public String getString(String columnLabel) {
        return getString(findColumn(columnLabel));
    }

    public java.sql.Time getTime(int columnIndex) {
        return null;
    }

    public java.sql.Time getTime(String columnLabel) {
        return getTime(findColumn(columnLabel));
    }

    public java.sql.Timestamp getTimestamp(int columnIndex) {
        return null;
    }

    public java.sql.Timestamp getTimestamp(String columnLabel) {
        return getTimestamp(findColumn(columnLabel));
    }

    public SQLWarning getWarnings() {
        return null;
    }

    public boolean isAfterLast() {
        return false;
    }

    public boolean isBeforeFirst() {
        return false;
    }

    public boolean isFirst() {
        return false;
    }

    public boolean isLast() {
        return false;
    }

    public boolean wasNull() {
        return false;
    }
}
