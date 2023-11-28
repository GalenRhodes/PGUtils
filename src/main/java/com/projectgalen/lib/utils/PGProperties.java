package com.projectgalen.lib.utils;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: PGProperties.java
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

import com.projectgalen.lib.utils.collections.ObjCache;
import com.projectgalen.lib.utils.errors.InvalidPropertyKeyValuePair;
import com.projectgalen.lib.utils.errors.PGPropertiesException;
import com.projectgalen.lib.utils.streams.Streams;
import com.projectgalen.lib.utils.text.Text;
import com.projectgalen.lib.utils.text.macro.Macro;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.projectgalen.lib.utils.helpers.Null.requireOrThrow;
import static java.util.Optional.ofNullable;

@SuppressWarnings("unused")
public class PGProperties extends Properties {

    private static final PGResourceBundle msgs  = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props = getXMLProperties("pg_properties.xml", PGProperties.class);

    static final Collector<String[], ?, Map<String, String>> KV_ARRAY_COLLECTOR = Collectors.toMap(k -> k[0], v -> v[1], (v1, v2) -> v2, LinkedHashMap::new);

    public static final @Language("RegExp") @RegExp String DEFAULT_LIST_SEPARATOR_RX = "\\s*,\\s*";
    public static final @Language("RegExp") @RegExp String DEFAULT_MAP_KV_RX         = "\\s*:\\s*";
    public static final                             String DEFAULT_DATE_FORMAT       = "yyyy-MM-dd";
    public static final                             String DEFAULT_TIME_FORMAT       = "HH:mm:ss.SSSZ";
    public static final                             String DEFAULT_DATETIME_FORMAT   = String.format("%s'T'%s", DEFAULT_DATE_FORMAT, DEFAULT_TIME_FORMAT);

    public PGProperties() {
        super();
    }

    public PGProperties(@Nullable Properties defaults) {
        super(defaults);
    }

    public void debug() {
        debugProperties(this, System.out);
    }

    public void debug(@NotNull PrintStream out) {
        debugProperties(this, out);
    }

    public String format(@NotNull @NonNls String key, Object... args) {
        return ofNullable(getProperty(key)).map(fmt -> String.format(fmt, args)).orElse(null);
    }

    public boolean getBoolean(@NotNull @NonNls String key, boolean defaultBoolean) {
        return switch(getProperty(key, Boolean.toString(defaultBoolean)).trim()) { case "true" -> true; case "false" -> false; default -> defaultBoolean; };/*@f0*/
    }/*@f1*/

    public boolean getBoolean(@NotNull @NonNls String key) {
        return getBoolean(key, false);
    }

    public byte getByte(@NotNull @NonNls String key, byte defaultValue) {
        try { return Byte.parseByte(prepForNumber(getProperty(key, String.valueOf(defaultValue)))); } catch(Exception e) { return defaultValue; }
    }

    public byte getByte(@NotNull @NonNls String key) {
        return getByte(key, (byte)0);
    }

    public @Contract("_,_,!null -> !null") Date getDateProperty(@NotNull @NonNls String key, @NotNull @NonNls String format, @Nullable Date defaultDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String           val = getProperty(key);
        if(val == null) return defaultDate;
        try { return sdf.parse(val); } catch(Exception e) { return defaultDate; }
    }

    public @Contract("_,!null -> !null") Date getDateProperty(@NotNull @NonNls String key, @Nullable Date defaultDate) {
        return getDateProperty(key, DEFAULT_DATETIME_FORMAT, defaultDate);
    }

    public Date getDateProperty(@NotNull @NonNls String key) {
        return getDateProperty(key, DEFAULT_DATETIME_FORMAT, null);
    }

    public double getDouble(@NotNull @NonNls String key, double defaultValue) {
        try { return Double.parseDouble(prepForNumber(getProperty(key, String.valueOf(defaultValue)))); } catch(Exception e) { return defaultValue; }
    }

    public double getDouble(@NotNull @NonNls String key) {
        return getDouble(key, 0);
    }

    public float getFloat(@NotNull @NonNls String key, float defaultValue) {
        try { return Float.parseFloat(prepForNumber(getProperty(key, String.valueOf(defaultValue)))); } catch(Exception e) { return defaultValue; }
    }

    public float getFloat(@NotNull @NonNls String key) {
        return getFloat(key, 0);
    }

    public int getInt(@NotNull @NonNls String key, int defaultValue) {
        try { return Integer.parseInt(prepForNumber(getProperty(key, String.valueOf(defaultValue)))); } catch(Exception e) { return defaultValue; }
    }

    public int getInt(@NotNull @NonNls String key) {
        return getInt(key, 0);
    }

    public @Contract("_,_,!null->!null") List<String> getList(@NotNull @NonNls String key, @NotNull @RegExp @Language("RegExp") String separator, List<String> defaultList) {
        return ofNullable(getProperty(key)).map(String::trim).map(s -> s.split(separator, -1)).map(Arrays::asList).orElse(defaultList);
    }

    public @Contract("_,!null->!null") List<String> getList(@NotNull @NonNls String key, List<String> defaultList) {
        return getList(key, DEFAULT_LIST_SEPARATOR_RX, defaultList);
    }

    public @NotNull List<String> getList(@NotNull @NonNls String key, @NotNull @RegExp @Language("RegExp") String separator) {
        return getList(key, separator, Collections.emptyList());
    }

    public @NotNull List<String> getList(@NotNull @NonNls String key) {
        return getList(key, DEFAULT_LIST_SEPARATOR_RX, Collections.emptyList());
    }

    public long getLong(@NotNull @NonNls String key, long defaultValue) {
        try { return Long.parseLong(prepForNumber(getProperty(key, String.valueOf(defaultValue)))); } catch(Exception e) { return defaultValue; }
    }

    public long getLong(@NotNull @NonNls String key) {
        return getLong(key, 0);
    }

    public @Contract("_,_,_,!null -> !null") Map<String, String> getMap(@NotNull @NonNls String key,
                                                                        @NotNull @NonNls @Language("RegExp") String listSeparator,
                                                                        @NotNull @NonNls @Language("RegExp") String kvSeparator,
                                                                        Map<String, String> defaultMap) {
        return ofNullable(getList(key, listSeparator, null)).map(Collection::stream).map(kvArrayMapFunction(kvSeparator)).orElse(Collections.emptyMap());
    }

    public @NotNull Map<String, String> getMap(@NotNull @NonNls String key, @NotNull @NonNls @Language("RegExp") String listSepPat, @NotNull @NonNls @Language("RegExp") String kvSepPat) {
        return getMap(key, listSepPat, kvSepPat, Collections.emptyMap());
    }

    public @Contract("_,!null -> !null") Map<String, String> getMap(@NotNull @NonNls String key, @Nullable Map<String, String> defaultMap) {
        return getMap(key, DEFAULT_LIST_SEPARATOR_RX, DEFAULT_MAP_KV_RX, defaultMap);
    }

    public @NotNull Map<String, String> getMap(@NotNull @NonNls String key) {
        return getMap(key, DEFAULT_LIST_SEPARATOR_RX, DEFAULT_MAP_KV_RX, Collections.emptyMap());
    }

    public @Override String getProperty(@NotNull @NonNls String key) {
        return getProperty(key, true);
    }

    public @Override @Contract("_,!null -> !null") String getProperty(@NotNull @NonNls String key, @Nullable String defaultValue) {
        return getProperty(key, defaultValue, true);
    }

    public @Contract("_,!null,_ -> !null") String getProperty(@NotNull @NonNls String key, @Nullable String defaultValue, boolean macroExpansion) {
        String value = _gp(key);
        return getString(((value == null) ? defaultValue : value), macroExpansion);
    }

    public String getProperty(@NotNull @NonNls String key, boolean macroExpansion) {
        return getString(_gp(key), macroExpansion);
    }

    public short getShort(@NotNull @NonNls String key, short defaultValue) {
        try { return Short.parseShort(prepForNumber(getProperty(key, String.valueOf(defaultValue)))); } catch(Exception e) { return defaultValue; }
    }

    public short getShort(@NotNull @NonNls String key) {
        return getShort(key, (short)0);
    }

    public @NotNull Stream<String> getStreamOf(@NotNull @NonNls String key) {
        return getStreamOf(key, DEFAULT_LIST_SEPARATOR_RX);
    }

    public @NotNull Stream<String> getStreamOf(@NotNull @NonNls String key, @NotNull @RegExp @Language("RegExp") @NonNls String regexp) {
        return Streams.splitStream(Text.tr(getProperty(key)), regexp);
    }

    private String _gp(@NotNull @NonNls String key) {
        return super.getProperty(key);
    }

    private String getString(String value, boolean macroExpansion) {
        return ((value == null) ? null : (macroExpansion ? Macro.replaceMacros(value, this::_gp) : value));
    }

    private @NotNull String prepForNumber(@NotNull String value) {
        return value.replaceAll("_", "").replaceAll(",", "");
    }

    public static void debugProperties(@NotNull Properties props) {
        debugProperties(props, System.out);
    }

    public static void debugProperties(@NotNull Properties props, @NotNull PrintStream out) {
        Set<String> propNames = props.stringPropertyNames();
        int         max1      = 0;
        int         max2      = 0;

        for(String name : propNames) {
            max1 = Math.max(max1, name.length());
            max2 = Math.max(max2, Objects.toString(props.getProperty(name), "").length());
        }

        String fmt = String.format("| %%%ds | %%%ds |\n", max1, max2);
        String bar = String.format("+%s+%s+\n", "-".repeat(max1 + 2), "-".repeat(max2 + 2));

        out.print(bar);
        for(String name : propNames) out.printf(fmt, name, Objects.toString(props.getProperty(name), ""));
        out.print(bar);
    }

    public static void debugSystemProperties() {
        debugSystemProperties(System.out);
    }

    public static void debugSystemProperties(@NotNull PrintStream out) {
        Properties sysProps = System.getProperties();
        debugProperties(sysProps, out);
    }

    public static @NotNull PGProperties getProperties(@NotNull @NonNls String resourceName, @NotNull Class<?> clazz) {
        return getProperties(resourceName, clazz, null);
    }

    public static @NotNull PGProperties getProperties(@NotNull @NonNls String resourceName, @Nullable Properties defaults) {
        return getProperties(resourceName, StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass(), defaults);
    }

    public static @NotNull PGProperties getProperties(@NotNull @NonNls String resourceName, @NotNull Class<?> clazz, @Nullable Properties defaults) {
        try { return getProperties(clazz.getResourceAsStream(resourceName), defaults); }
        catch(Exception e) { throw new PGPropertiesException(msgs.format("msg.err.props.load.failed.res", resourceName), e); }
    }

    public static @NotNull PGProperties getProperties(@NotNull File file) {
        return getProperties(file, null);
    }

    public static @NotNull PGProperties getProperties(@NotNull File file, @Nullable Properties defaults) {
        try(InputStream inputStream = new FileInputStream(file)) { return getProperties(inputStream, defaults); }
        catch(Exception e) { throw new PGPropertiesException(msgs.format("msg.err.props.load.failed.file", file), e); }
    }

    public static @NotNull PGProperties getProperties(InputStream inputStream) {
        return getProperties(inputStream, new Properties());
    }

    public static @NotNull PGProperties getProperties(InputStream inputStream, @Nullable Properties defaults) {
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(requireOrThrow(inputStream, PGProperties::missingInputStream))) {
            PGProperties props = new PGProperties(defaults);
            props.load(bufferedInputStream);
            return props;
        }
        catch(Exception e) {
            throw new PGPropertiesException(msgs.getString("msg.err.props.load.failed"), e);
        }
    }

    public static @NotNull PGProperties getProperties(Reader reader) {
        return getProperties(reader, null);
    }

    public static @NotNull PGProperties getProperties(Reader reader, @Nullable Properties defaults) {
        try(BufferedReader bufferedReader = new BufferedReader(requireOrThrow(reader, PGProperties::missingReader))) {
            PGProperties props = new PGProperties(defaults);
            props.load(bufferedReader);
            return props;
        }
        catch(Exception e) {
            throw new PGPropertiesException(msgs.getString("msg.err.props.load.failed"), e);
        }
    }

    public static @NotNull PGProperties getXMLProperties(InputStream inputStream) {
        return getXMLProperties(inputStream, null);
    }

    public static @NotNull PGProperties getXMLProperties(InputStream inputStream, @Nullable Properties defaults) {
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(requireOrThrow(inputStream, PGProperties::missingInputStream))) {
            PGProperties props = new PGProperties(defaults);
            props.loadFromXML(bufferedInputStream);
            return props;
        }
        catch(Exception e) {
            throw new PGPropertiesException(msgs.getString("msg.err.props.load.failed"), e);
        }
    }

    public static @NotNull PGProperties getXMLProperties(@NotNull File file) {
        return getXMLProperties(file, null);
    }

    public static @NotNull PGProperties getXMLProperties(@NotNull File file, @Nullable Properties defaults) {
        try(InputStream inputStream = new FileInputStream(file)) { return getXMLProperties(inputStream, defaults); }
        catch(Exception e) { throw new PGPropertiesException(msgs.format("msg.err.props.load.failed.file", file), e); }
    }

    public static @NotNull PGProperties getXMLProperties(@NotNull @NonNls String resourceName, @NotNull Class<?> clazz) {
        return getXMLProperties(resourceName, clazz, null);
    }

    public static @NotNull PGProperties getXMLProperties(@NotNull @NonNls String resourceName, @Nullable Properties defaults) {
        Class<?> clazz = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        return getXMLProperties(resourceName, clazz, defaults);
    }

    public static @NotNull PGProperties getXMLProperties(@NotNull @NonNls String resourceName, @NotNull Class<?> clazz, @Nullable Properties defaults) {
        try { return getXMLProperties(clazz.getResourceAsStream(resourceName), defaults); }
        catch(Exception e) { throw new PGPropertiesException(msgs.format("msg.err.props.load.failed.res", resourceName), e); }
    }

    private static String @NotNull [] checkKvPair(@NotNull String kv, String @NotNull [] ar) {
        if(ar.length == 2) return ar;
        throw new InvalidPropertyKeyValuePair(msgs.format("msg.err.macro.kv_pair_missing_value", kv));
    }

    private static @NotNull Function<Stream<String>, Map<String, String>> kvArrayMapFunction(@Language("RegExp") @NonNls String kvSeparator) {
        return stream -> stream.map(kvSplitMapFunction(kvSeparator)).collect(KV_ARRAY_COLLECTOR);
    }

    private static @NotNull Function<String, String[]> kvSplitMapFunction(@Language("RegExp") @NonNls String kvSeparator) {
        return kvString -> checkKvPair(kvString, kvString.split(kvSeparator, 2));
    }

    private static @NotNull PGPropertiesException missingInputStream() {
        return new PGPropertiesException(msgs.getString("msg.err.props.missing.input.stream"), new NullPointerException());
    }

    private static @NotNull PGPropertiesException missingReader() {
        return new PGPropertiesException(msgs.getString("msg.err.props.missing.reader"), new NullPointerException());
    }

    private static final class CacheHolder {
        private static final ObjCache CACHE = new ObjCache();
    }
}
