package com.projectgalen.lib.utils;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: JDBC.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: November 11, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import com.projectgalen.lib.utils.delegates.ThrowingFunction;
import com.projectgalen.lib.utils.delegates.ThrowingTriConsumer;
import com.projectgalen.lib.utils.errors.SQLRuntimeException;
import com.projectgalen.lib.utils.io.ReaderInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.sql.Types.*;

public final class JDBC {
    private JDBC() { }

    public static @NotNull PreparedStatement prepStmt(@NotNull PreparedStatement stmt, Object @NotNull ... params) throws SQLRuntimeException {
        return prepStmt(stmt, StandardCharsets.UTF_8, params);
    }

    public static @NotNull PreparedStatement prepStmt(@NotNull PreparedStatement stmt, Charset cs, Object @NotNull [] params) {
        return SQLRuntimeException.get(() -> {
            int               idx = 0;
            ParameterMetaData md  = stmt.getParameterMetaData();

            for(Object o : params) {
                int pt = md.getParameterType(++idx);

                if(o == null) {
                    stmt.setNull(idx, pt);
                }
                else switch(pt) {/*@f0*/
                    case CHAR,  VARCHAR,  LONGVARCHAR  -> stmt.setCharacterStream( idx, getReader(pt, o, cs));
                    case NCHAR, NVARCHAR, LONGNVARCHAR -> stmt.setNCharacterStream(idx, getReader(pt, o, cs));
                    case CLOB                          -> stmt.setClob(            idx, getReader(pt, o, cs));
                    case NCLOB                         -> stmt.setNClob(           idx, getReader(pt, o, cs));

                    case BIGINT           -> { if(o instanceof Number num) stmt.setBigDecimal(idx, getBigIntAsBigDecimal(num)); else stmt.setString(idx, getString(o, cs)); }
                    case DECIMAL, NUMERIC -> { if(o instanceof Number num) stmt.setBigDecimal(idx, getBigDecimal(num));         else stmt.setString(idx, getString(o, cs)); }
                    case DOUBLE,  REAL    -> { if(o instanceof Number num) stmt.setDouble(    idx, num.doubleValue());          else stmt.setString(idx, getString(o, cs)); }
                    case FLOAT            -> { if(o instanceof Number num) stmt.setFloat(     idx, num.floatValue());           else stmt.setString(idx, getString(o, cs)); }
                    case INTEGER          -> { if(o instanceof Number num) stmt.setInt(       idx, num.intValue());             else stmt.setString(idx, getString(o, cs)); }
                    case SMALLINT         -> { if(o instanceof Number num) stmt.setShort(     idx, num.shortValue());           else stmt.setString(idx, getString(o, cs)); }
                    case TINYINT          -> { if(o instanceof Number num) stmt.setByte(      idx, num.byteValue());            else stmt.setString(idx, getString(o, cs)); }

                    case DATE                               -> setDate(stmt, idx, o, cs, (s, i, d) -> s.setDate(     i, (d instanceof java.sql.Date t) ? t : new java.sql.Date(d.getTime())));
                    case TIME,      TIME_WITH_TIMEZONE      -> setDate(stmt, idx, o, cs, (s, i, d) -> s.setTime(     i, (d instanceof Time t)          ? t : new Time(         d.getTime())));
                    case TIMESTAMP, TIMESTAMP_WITH_TIMEZONE -> setDate(stmt, idx, o, cs, (s, i, d) -> s.setTimestamp(i, (d instanceof Timestamp t)     ? t : new Timestamp(    d.getTime())));

                    case BINARY, VARBINARY, LONGVARBINARY -> stmt.setBinaryStream(idx, getInputStream(o, cs));
                }/*@f1*/
            }

            return stmt;
        });
    }

    public static <R> @NotNull Stream<R> streamResultSet(@NotNull ResultSet rs, @NotNull ThrowingFunction<ResultSet, R, SQLException> function) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator<>(rs, function), Spliterator.IMMUTABLE | Spliterator.ORDERED), false);
    }

    private static @NotNull BigDecimal getBigDecimal(Number num) {
        return ((num instanceof BigDecimal d) ? d : (((num instanceof BigInteger i) ? new BigDecimal(i) : BigDecimal.valueOf(num.doubleValue()))));
    }

    private static @NotNull BigDecimal getBigIntAsBigDecimal(Number num) {
        return ((num instanceof BigDecimal d) ? d.setScale(0, RoundingMode.HALF_UP) : new BigDecimal(getBigInteger(num)));
    }

    private static @NotNull BigInteger getBigInteger(Number num) {
        return ((num instanceof BigInteger i) ? i : BigInteger.valueOf(num.longValue()));
    }

    private static @NotNull InputStream getInputStream(Object p, Charset cs) {/*@f0*/
        return ((p instanceof InputStream inputStream) ? inputStream                             :
                ((p instanceof Reader reader)          ? new ReaderInputStream(reader, true, cs) :
                                                         new ByteArrayInputStream((p instanceof byte[] b) ? b : ((p instanceof char[] c) ? String.valueOf(c) : p.toString()).getBytes(cs))));
    }/*@f1*/

    private static @NotNull Reader getReader(int paramType, Object p, Charset cs) {/*@f0*/
        return ((p instanceof Reader reader)   ? reader                                                  :
                ((p instanceof InputStream is) ? new InputStreamReader(is, cs)                           :
                 ((p instanceof char[] cb)     ? new CharArrayReader(cb)                                 :
                  ((p instanceof byte[] bb)    ? new InputStreamReader(new ByteArrayInputStream(bb), cs) :
                                                 new StringReader(p.toString())))));
    }/*@f1*/

    private static @NotNull String getString(Object p, Charset cs) {
        return (p instanceof char[] a) ? String.valueOf(a) : ((p instanceof byte[] a) ? new String(a, cs) : p.toString());
    }

    private static void setDate(PreparedStatement stmt, int idx, Object p, Charset cs, ThrowingTriConsumer<PreparedStatement, Integer, Date, SQLException> consumer) throws SQLException {
        if(p instanceof Date date) consumer.accept(stmt, idx, date);
        else if(p instanceof Calendar cal) consumer.accept(stmt, idx, cal.getTime());
        else stmt.setString(idx, getString(p, cs));
    }

    private static void setNumber(PreparedStatement stmt, int idx, Object p, Charset cs, ThrowingTriConsumer<PreparedStatement, Integer, Number, SQLException> consumer) throws SQLException {
        if(p instanceof Number num) consumer.accept(stmt, idx, num);
        else stmt.setString(idx, getString(p, cs));
    }

    private static final class ResultSetIterator<R> implements Iterator<R> {
        private final @NotNull ResultSet                                    _rs;
        private final @NotNull ThrowingFunction<ResultSet, R, SQLException> _function;
        private                Boolean                                      _hasNext = null;

        public ResultSetIterator(@NotNull ResultSet rs, @NotNull ThrowingFunction<ResultSet, R, SQLException> function) {
            this._rs       = rs;
            this._function = function;
        }

        public @Override boolean hasNext() {
            synchronized(_function) { return _hasNext(); }
        }

        public @Override @NotNull R next() {
            synchronized(_function) {
                if(!_hasNext()) throw new NoSuchElementException();
                try { return _function.apply(_rs); }
                catch(SQLException e) { throw new SQLRuntimeException(e); }
                finally { _hasNext = null; }
            }
        }

        private Boolean _hasNext() {
            if(_hasNext == null) try { _hasNext = _rs.next(); } catch(SQLException e) { throw new SQLRuntimeException(e); }
            return _hasNext;
        }
    }
}
