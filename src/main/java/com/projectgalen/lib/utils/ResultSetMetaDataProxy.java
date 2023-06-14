package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: ResultSetMetaDataProxy.java
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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class ResultSetMetaDataProxy {
    /**
     * The constant indicating that a column does not allow <code>NULL</code> values.
     */
    public static final int columnNoNulls         = 0;
    /**
     * The constant indicating that a column allows <code>NULL</code> values.
     */
    public static final int columnNullable        = 1;
    /**
     * The constant indicating that the nullability of a column's values is unknown.
     */
    public static final int columnNullableUnknown = 2;
    protected final int columnCount;
    protected final boolean[] isAutoIncrement;
    protected final boolean[] isCaseSensitive;
    protected final boolean[] isSearchable;
    protected final boolean[] isCurrency;
    protected final boolean[] isSigned;
    protected final boolean[] isReadOnly;
    protected final boolean[] isWritable;
    protected final boolean[] isDefinitelyWritable;
    protected final String[] tableName;
    protected final String[] catalogName;
    protected final String[] columnTypeName;
    protected final String[] columnClassName;
    protected final String[] columnLabel;
    protected final String[] columnName;
    protected final String[] schemaName;
    protected final int[] columnDisplaySize;
    protected final int[] columnType;
    protected final int[] precision;
    protected final int[] scale;
    protected final int[] isNullable;

    public ResultSetMetaDataProxy(@NotNull ResultSetMetaData rsmd) throws SQLException {
        this.columnCount          = rsmd.getColumnCount();
        this.isAutoIncrement      = getBooleanField(rsmd::isAutoIncrement);
        this.isCaseSensitive      = getBooleanField(rsmd::isCaseSensitive);
        this.isSearchable         = getBooleanField(rsmd::isSearchable);
        this.isCurrency           = getBooleanField(rsmd::isCurrency);
        this.isSigned             = getBooleanField(rsmd::isSigned);
        this.isWritable           = getBooleanField(rsmd::isWritable);
        this.isReadOnly           = getBooleanField(rsmd::isReadOnly);
        this.isDefinitelyWritable = getBooleanField(rsmd::isDefinitelyWritable);

        this.tableName       = getStringField(rsmd::getTableName);
        this.catalogName     = getStringField(rsmd::getCatalogName);
        this.columnTypeName  = getStringField(rsmd::getColumnTypeName);
        this.columnClassName = getStringField(rsmd::getColumnClassName);
        this.columnLabel     = getStringField(rsmd::getColumnLabel);
        this.columnName      = getStringField(rsmd::getColumnName);
        this.schemaName      = getStringField(rsmd::getSchemaName);

        this.columnDisplaySize = getIntField(rsmd::getColumnDisplaySize);
        this.columnType        = getIntField(rsmd::getColumnType);
        this.precision         = getIntField(rsmd::getPrecision);
        this.scale             = getIntField(rsmd::getScale);
        this.isNullable        = getIntField(rsmd::isNullable);
    }

    /**
     * Gets the designated column's table's catalog name.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return the name of the catalog for the table in which the given column appears or "" if not applicable
     */
    public String getCatalogName(int column) {
        return catalogName[column - 1];
    }

    /**
     * <p>Returns the fully-qualified name of the Java class whose instances
     * are manufactured if the method <code>ResultSet.getObject</code> is called to retrieve a value from the column.  <code>ResultSet.getObject</code> may return a subclass of the class returned by
     * this method.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return the fully-qualified name of the class in the Java programming language that would be used by the method
     * <code>ResultSet.getObject</code> to retrieve the value in the specified
     * column. This is the class name used for custom mapping.
     *
     * @since 1.2
     */
    public String getColumnClassName(int column) {
        return columnClassName[column - 1];
    }

    /**
     * Returns the number of columns in this <code>ResultSet</code> object.
     *
     * @return the number of columns
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Indicates the designated column's normal maximum width in characters.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return the normal maximum number of characters allowed as the width of the designated column
     */
    public int getColumnDisplaySize(int column) {
        return columnDisplaySize[column - 1];
    }

    /**
     * Gets the designated column's suggested title for use in printouts and displays. The suggested title is usually specified by the SQL <code>AS</code> clause.  If a SQL <code>AS</code> is not
     * specified, the value returned from
     * <code>getColumnLabel</code> will be the same as the value returned by the
     * <code>getColumnName</code> method.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return the suggested column title
     */
    public String getColumnLabel(int column) {
        return columnLabel[column - 1];
    }

    /**
     * Get the designated column's name.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return column name
     */
    public String getColumnName(int column) {
        return columnName[column - 1];
    }

    /**
     * Retrieves the designated column's SQL type.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return SQL type from java.sql.Types
     *
     * @see Types
     */
    public int getColumnType(int column) {
        return columnType[column - 1];
    }

    /**
     * Retrieves the designated column's database-specific type name.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return type name used by the database. If the column type is a user-defined type, then a fully-qualified type name is returned.
     */
    public String getColumnTypeName(int column) {
        return columnTypeName[column - 1];
    }

    /**
     * Get the designated column's specified column size. For numeric data, this is the maximum precision.  For character data, this is the length in characters. For datetime datatypes, this is the
     * length in characters of the String representation (assuming the maximum allowed precision of the fractional seconds component). For binary data, this is the length in bytes.  For the ROWID
     * datatype, this is the length in bytes. 0 is returned for data types where the column size is not applicable.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return precision
     */
    public int getPrecision(int column) {
        return precision[column - 1];
    }

    /**
     * Gets the designated column's number of digits to right of the decimal point. 0 is returned for data types where the scale is not applicable.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return scale
     */
    public int getScale(int column) {
        return scale[column - 1];
    }

    /**
     * Get the designated column's table's schema.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return schema name or "" if not applicable
     */
    public String getSchemaName(int column) {
        return schemaName[column - 1];
    }

    /**
     * Gets the designated column's table name.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return table name or "" if not applicable
     */
    public String getTableName(int column) {
        return tableName[column - 1];
    }

    /**
     * Indicates whether the designated column is automatically numbered.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isAutoIncrement(int column) {
        return isAutoIncrement[column - 1];
    }

    /**
     * Indicates whether a column's case matters.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isCaseSensitive(int column) {
        return isCaseSensitive[column - 1];
    }

    /**
     * Indicates whether the designated column is a cash value.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isCurrency(int column) {
        return isCurrency[column - 1];
    }

    /**
     * Indicates whether a write on the designated column will definitely succeed.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isDefinitelyWritable(int column) {
        return isDefinitelyWritable[column - 1];
    }

    /**
     * Indicates the nullability of values in the designated column.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return the nullability status of the given column; one of <code>columnNoNulls</code>,
     * <code>columnNullable</code> or <code>columnNullableUnknown</code>
     */
    public int isNullable(int column) {
        return isNullable[column - 1];
    }

    /**
     * Indicates whether the designated column is definitely not writable.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isReadOnly(int column) {
        return isReadOnly[column - 1];
    }

    /**
     * Indicates whether the designated column can be used in a where clause.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isSearchable(int column) {
        return isSearchable[column - 1];
    }

    /**
     * Indicates whether values in the designated column are signed numbers.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isSigned(int column) {
        return isSigned[column - 1];
    }

    /**
     * Indicates whether it is possible for a write on the designated column to succeed.
     *
     * @param column the first column is 1, the second is 2, ...
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean isWritable(int column) {
        return isWritable[column - 1];
    }

    private boolean @NotNull [] getBooleanField(Getter<Boolean> delegate) throws SQLException {
        boolean[] out = new boolean[columnCount];
        for(int i = 0; i < columnCount; i++) out[i] = delegate.get(i + 1);
        return out;
    }

    private int @NotNull [] getIntField(Getter<Integer> delegate) throws SQLException {
        int[] out = new int[columnCount];
        for(int i = 0; i < columnCount; i++) out[i] = delegate.get(i + 1);
        return out;
    }

    private String @NotNull [] getStringField(Getter<String> delegate) throws SQLException {
        String[] out = new String[columnCount];
        for(int i = 0; i < columnCount; i++) out[i] = delegate.get(i + 1);
        return out;
    }

    private interface Getter<T> {
        @NotNull T get(int i) throws SQLException;
    }
}
