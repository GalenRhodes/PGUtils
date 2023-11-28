package com.projectgalen.lib.utils.jdbc;
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

import com.projectgalen.lib.utils.delegates.ThrowingQuadConsumer;
import com.projectgalen.lib.utils.delegates.ThrowingTriConsumer;
import com.projectgalen.lib.utils.delegates.ThrowingTriFunction;
import com.projectgalen.lib.utils.errors.SQLRuntimeException;
import com.projectgalen.lib.utils.io.ReaderInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private static final ThrowingQuadConsumer<PreparedStatement, ParameterMetaData, Integer, Object, SQLException> DEFAULT_OTHER_HANDLER = (s, m, i, o) -> s.setObject(i, o, m.getParameterType(i));
    private static final Charset                                                                                   DEFAULT_CHARSET       = StandardCharsets.UTF_8;

    private JDBC() { }

    public static @NotNull List<Object[]> getResults(@NotNull ResultSet resultSet) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        while(resultSet.next()) list.add(getRow(resultSet));
        return list;
    }

    public static Object @NotNull [] getRow(@NotNull ResultSet resultSet) throws SQLException {
        int      rcc = resultSet.getMetaData().getColumnCount();
        Object[] row = new Object[rcc];
        for(int c = 0; c < rcc; ++c) row[c] = resultSet.getObject(c + 1);
        return row;
    }

    public static @NotNull PreparedStatement prepStmt(@NotNull PreparedStatement stmt, Object @NotNull ... params) throws SQLRuntimeException {
        return prepStmt(stmt, DEFAULT_OTHER_HANDLER, params);
    }

    public static @NotNull PreparedStatement prepStmt(@NotNull PreparedStatement stmt,
                                                      @NotNull ThrowingQuadConsumer<PreparedStatement, ParameterMetaData, Integer, Object, SQLException> otherHandler,
                                                      Object @NotNull ... params) throws SQLRuntimeException {
        return prepStmt(stmt, DEFAULT_CHARSET, params);
    }

    public static @NotNull PreparedStatement prepStmt(@NotNull PreparedStatement stmt, Charset cs, Object @NotNull ... params) {
        return prepStmt(stmt, cs, DEFAULT_OTHER_HANDLER, params);
    }

    public static @NotNull PreparedStatement prepStmt(@NotNull PreparedStatement stmt,
                                                      Charset cs,
                                                      @NotNull ThrowingQuadConsumer<PreparedStatement, ParameterMetaData, Integer, Object, SQLException> otherHandler,
                                                      Object @NotNull ... params) {
        return SQLRuntimeException.get(() -> {
            ParameterMetaData md = stmt.getParameterMetaData();
            for(int idx = 1, j = 0; idx <= params.length; ++idx) setValue(stmt, md, idx, params[j++], cs, otherHandler);
            return stmt;
        });
    }

    public static <R> @NotNull Stream<R> streamResultSet(@NotNull ResultSet rs, @NotNull ThrowingTriFunction<ResultSet, ResultSetMetaData, Long, R, SQLException> function) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator<>(rs, function), Spliterator.IMMUTABLE | Spliterator.ORDERED), false);
    }

    private static @NotNull BigDecimal getBigDecimal(Number number) {
        return ((number instanceof BigDecimal d) ? d : (((number instanceof BigInteger i) ? new BigDecimal(i) : BigDecimal.valueOf(number.doubleValue()))));
    }

    private static @NotNull BigDecimal getBigInteger(Number number) {
        return number instanceof BigDecimal d ? (d.setScale(0, RoundingMode.HALF_UP)) : (new BigDecimal(((number instanceof BigInteger i) ? i : BigInteger.valueOf(number.longValue()))));
    }

    private static byte @NotNull [] getBytes(Object p, Charset cs) {
        return ((p instanceof byte[] b) ? (b) : ((p instanceof char[] c) ? String.valueOf(c) : p.toString()).getBytes(cs));
    }

    private static @NotNull InputStream getInputStream(Object p, Charset cs) {/*@f0*/
        return ((p instanceof InputStream inputStream) ? inputStream                             :
                ((p instanceof Reader reader)          ? new ReaderInputStream(reader, true, cs) :
                                                         new ByteArrayInputStream(getBytes(p, cs))));
    }/*@f1*/

    private static @NotNull Reader getReader(Object p, Charset cs) {/*@f0*/
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

    private static void setValue(@NotNull PreparedStatement preparedStatement,
                                 @NotNull ParameterMetaData parameterMetaData,
                                 int parameterIndex,
                                 @Nullable Object obj,
                                 @NotNull Charset charset,
                                 @NotNull ThrowingQuadConsumer<PreparedStatement, ParameterMetaData, Integer, Object, SQLException> otherHandler) throws SQLException {
        if(obj == null) {
            preparedStatement.setNull(parameterIndex, parameterMetaData.getParameterType(parameterIndex));
        }
        else switch(parameterMetaData.getParameterType(parameterIndex)) {/*@f0*/
            case CHAR,  VARCHAR,  LONGVARCHAR       -> preparedStatement.setCharacterStream( parameterIndex, getReader(obj, charset));
            case NCHAR, NVARCHAR, LONGNVARCHAR      -> preparedStatement.setNCharacterStream(parameterIndex, getReader(obj, charset));
            case CLOB                               -> preparedStatement.setClob(            parameterIndex, getReader(obj, charset));
            case NCLOB                              -> preparedStatement.setNClob(           parameterIndex, getReader(obj, charset));
            case BIGINT                             -> setNumber(preparedStatement, parameterIndex, obj, charset, (s, i, n) -> s.setBigDecimal(i, getBigInteger(n)              ));
            case DECIMAL, NUMERIC                   -> setNumber(preparedStatement, parameterIndex, obj, charset, (s, i, n) -> s.setBigDecimal(i, getBigDecimal(n)              ));
            case DOUBLE,  REAL                      -> setNumber(preparedStatement, parameterIndex, obj, charset, (s, i, n) -> s.setDouble(    i, n.doubleValue()               ));
            case FLOAT                              -> setNumber(preparedStatement, parameterIndex, obj, charset, (s, i, n) -> s.setFloat(     i, n.floatValue()                ));
            case INTEGER                            -> setNumber(preparedStatement, parameterIndex, obj, charset, (s, i, n) -> s.setInt(       i, n.intValue()                  ));
            case SMALLINT                           -> setNumber(preparedStatement, parameterIndex, obj, charset, (s, i, n) -> s.setShort(     i, n.shortValue()                ));
            case TINYINT                            -> setNumber(preparedStatement, parameterIndex, obj, charset, (s, i, n) -> s.setByte(      i, n.byteValue()                 ));
            case DATE                               -> setDate(  preparedStatement, parameterIndex, obj, charset, (s, i, d) -> s.setDate(      i, new java.sql.Date(d.getTime())));
            case TIME,      TIME_WITH_TIMEZONE      -> setDate(  preparedStatement, parameterIndex, obj, charset, (s, i, d) -> s.setTime(      i, new Time(         d.getTime())));
            case TIMESTAMP, TIMESTAMP_WITH_TIMEZONE -> setDate(  preparedStatement, parameterIndex, obj, charset, (s, i, d) -> s.setTimestamp( i, new Timestamp(    d.getTime())));
            case BINARY, VARBINARY, LONGVARBINARY   -> preparedStatement.setBinaryStream(parameterIndex, getInputStream(obj, charset));
            default                                 -> otherHandler.accept(preparedStatement, parameterMetaData, parameterIndex, obj);
        }/*@f1*/
    }

    private static final class ResultSetIterator<R> implements Iterator<R> {
        private final @NotNull ResultSet                                                                _resultSet;
        private final @NotNull ResultSetMetaData                                                        _metaData;
        private final @NotNull ThrowingTriFunction<ResultSet, ResultSetMetaData, Long, R, SQLException> _function;
        private                Boolean                                                                  _hasNext   = null;
        private                long                                                                     _rowNumber = 0;

        public ResultSetIterator(@NotNull ResultSet resultSet, @NotNull ThrowingTriFunction<ResultSet, ResultSetMetaData, Long, R, SQLException> function) {
            this._resultSet = resultSet;
            this._hasNext   = SQLRuntimeException.get(resultSet::next);
            this._metaData  = SQLRuntimeException.get(resultSet::getMetaData);
            this._function  = function;
        }

        public @Override boolean hasNext() {
            synchronized(this) { return _hasNext(); }
        }

        public @Override @NotNull R next() {
            synchronized(this) {
                if(_hasNext()) return SQLRuntimeException.get(() -> _function.apply(_resultSet, _metaData, ++_rowNumber), () -> _hasNext = null);
                else throw new NoSuchElementException();
            }
        }

        private Boolean _hasNext() {
            if(_hasNext == null) _hasNext = SQLRuntimeException.get(_resultSet::next);
            return _hasNext;
        }
    }
}
