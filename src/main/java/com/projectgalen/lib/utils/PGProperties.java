package com.projectgalen.lib.utils;

import com.projectgalen.lib.utils.errors.InvalidPropertyKeyValuePair;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public class PGProperties extends Properties {

    private static final PGResourceBundle _msgs         = PGResourceBundle.getSharedBundle("com.projectgalen.lib.utils.messages");
    private static final int              DEFAULT_LIMIT = -1;

    public static final @Language("RegExp") String DEFAULT_LIST_SEPARATOR_PATTERN = "\\s*,\\s*";
    public static final @Language("RegExp") String DEFAULT_MAP_KV_PATTERN         = "\\s*:\\s*";
    public static final                     String DEFAULT_DATE_FORMAT            = "yyyy-MM-dd";
    public static final                     String DEFAULT_TIME_FORMAT            = "HH:mm:ss.SSSZ";
    public static final                     String DEFAULT_DATETIME_FORMAT        = String.format("%s'T'%s", DEFAULT_DATE_FORMAT, DEFAULT_TIME_FORMAT);

    public <T extends Throwable> PGProperties(@Nullable InputStream inputStream, Class<T> errorClass) throws T {
        super();
        try {
            if(inputStream == null) throw U.getThrowable(_msgs.getString("msg.err.no_input_stream"), errorClass);
            try(inputStream) { load(inputStream); }
        }
        catch(Exception e) { throw U.wrapThrowable(_msgs.getString("msg.err.props_load_failed"), e, errorClass); }
    }

    public PGProperties(@Nullable InputStream inputStream) throws IllegalArgumentException {
        this(inputStream, IllegalArgumentException.class);
    }

    public String format(@NotNull @NonNls String key, Object... args) {
        String fmt = getProperty(key);
        return ((fmt == null) ? null : String.format(fmt, args));
    }

    public boolean getBooleanProperty(@NotNull @NonNls String key, boolean defaultBoolean) {
        switch(getProperty(key, Boolean.toString(defaultBoolean)).trim()) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                return defaultBoolean;
        }
    }

    public boolean getBooleanProperty(@NotNull @NonNls String key) {
        return getBooleanProperty(key, false);
    }

    public byte getByteProperty(@NotNull @NonNls String key, byte defaultValue) {
        try {
            return Byte.parseByte(prepForNumber(getProperty(key, String.valueOf(defaultValue))));
        }
        catch(Exception e) {
            return defaultValue;
        }
    }

    public byte getByteProperty(@NotNull @NonNls String key) {
        return getByteProperty(key, (byte)0);
    }

    public Date getDateProperty(@NotNull @NonNls String key, @NotNull @NonNls String format, @Nullable Date defaultDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String           val = getProperty(key);
        if(val == null) return defaultDate;
        try { return sdf.parse(val); }
        catch(Exception e) { return defaultDate; }
    }

    public Date getDateProperty(@NotNull @NonNls String key, @Nullable Date defaultDate) {
        return getDateProperty(key, DEFAULT_DATETIME_FORMAT, defaultDate);
    }

    public Date getDateProperty(@NotNull @NonNls String key) {
        return getDateProperty(key, DEFAULT_DATETIME_FORMAT, null);
    }

    public double getDoubleProperty(@NotNull @NonNls String key, double defaultValue) {
        try {
            return Double.parseDouble(prepForNumber(getProperty(key, String.valueOf(defaultValue))));
        }
        catch(Exception e) {
            return defaultValue;
        }
    }

    public double getDoubleProperty(@NotNull @NonNls String key) {
        return getDoubleProperty(key, 0);
    }

    public float getFloatProperty(@NotNull @NonNls String key, float defaultValue) {
        try {
            return Float.parseFloat(prepForNumber(getProperty(key, String.valueOf(defaultValue))));
        }
        catch(Exception e) {
            return defaultValue;
        }
    }

    public float getFloatProperty(@NotNull @NonNls String key) {
        return getFloatProperty(key, 0);
    }

    public int getIntProperty(@NotNull @NonNls String key, int defaultValue) {
        try {
            return Integer.parseInt(prepForNumber(getProperty(key, String.valueOf(defaultValue))));
        }
        catch(Exception e) {
            return defaultValue;
        }
    }

    public int getIntProperty(@NotNull @NonNls String key) {
        return getIntProperty(key, 0);
    }

    public List<String> getList(@NotNull @NonNls String key, @Nullable @Language("RegExp") String separatorPattern, int limit, @Nullable List<String> defaultList) {
        String str = getProperty(key);
        if(str == null) return defaultList;
        String[] arr = str.split(U.ifNull(separatorPattern, DEFAULT_LIST_SEPARATOR_PATTERN), limit);
        return Arrays.asList(arr);
    }

    public List<String> getList(@NotNull @NonNls String key, @Nullable @Language("RegExp") @NonNls String separatorPattern, @Nullable List<String> defaultList) {
        return getList(key, separatorPattern, DEFAULT_LIMIT, defaultList);
    }

    public List<String> getList(@NotNull @NonNls String key, int limit, @Nullable List<String> defaultList) {
        return getList(key, DEFAULT_LIST_SEPARATOR_PATTERN, limit, defaultList);
    }

    public List<String> getList(@NotNull @NonNls String key, int limit) {
        return getList(key, DEFAULT_LIST_SEPARATOR_PATTERN, limit, null);
    }

    public List<String> getList(@NotNull @NonNls String key, @Nullable List<String> defaultList) {
        return getList(key, DEFAULT_LIST_SEPARATOR_PATTERN, DEFAULT_LIMIT, defaultList);
    }

    public List<String> getList(@NotNull @NonNls String key) {
        return getList(key, DEFAULT_LIST_SEPARATOR_PATTERN, DEFAULT_LIMIT, null);
    }

    public long getLongProperty(@NotNull @NonNls String key, long defaultValue) {
        try {
            return Long.parseLong(prepForNumber(getProperty(key, String.valueOf(defaultValue))));
        }
        catch(Exception e) {
            return defaultValue;
        }
    }

    public long getLongProperty(@NotNull @NonNls String key) {
        return getLongProperty(key, 0);
    }

    public Map<String, String> getMap(@NotNull @NonNls String key,
                                      @NotNull @NonNls @Language("RegExp") String listSeparatorPattern,
                                      @NotNull @NonNls @Language("RegExp") String kvSeparatorPattern,
                                      @Nullable Map<String, String> defaultMap) {
        List<String> list = getList(key, listSeparatorPattern, 0, null);
        if(list == null) return defaultMap;
        Map<String, String> map = new LinkedHashMap<>();

        for(String kv : list) {
            String[] arKv = kv.split(kvSeparatorPattern, 2);
            if(arKv.length != 2) throw new InvalidPropertyKeyValuePair(_msgs.format("msg.err.kv_pair_missing_value", kv));
            map.put(arKv[0], arKv[1]);
        }

        return map;
    }

    public Map<String, String> getMap(@NotNull @NonNls String key, @Nullable Map<String, String> defaultMap) {
        return getMap(key, DEFAULT_LIST_SEPARATOR_PATTERN, DEFAULT_MAP_KV_PATTERN, defaultMap);
    }

    public Map<String, String> getMap(@NotNull @NonNls String key) {
        return getMap(key, DEFAULT_LIST_SEPARATOR_PATTERN, DEFAULT_MAP_KV_PATTERN, null);
    }

    @Override
    public String getProperty(String key) {
        return Macro.replaceMacros(_gp(key), this::_gp);
    }

    public short getShortProperty(@NotNull @NonNls String key, short defaultValue) {
        try {
            return Short.parseShort(prepForNumber(getProperty(key, String.valueOf(defaultValue))));
        }
        catch(Exception e) {
            return defaultValue;
        }
    }

    public short getShortProperty(@NotNull @NonNls String key) {
        return getShortProperty(key, (short)0);
    }

    private String _gp(String key) {
        return super.getProperty(key);
    }

    private String prepForNumber(@NotNull String value) {
        return value.replaceAll("_", "").replaceAll(",", "");
    }

    public static @NotNull PGProperties getSharedInstanceForNamedResource(@NotNull @NonNls String resourceName, @NotNull Class<?> refClass) {
        String       key   = String.format("%s|%s", refClass.getName(), resourceName);
        PGProperties props = CacheHolder.CACHE.get(key, PGProperties.class);

        if(props != null) return props;
        props = new PGProperties(refClass.getResourceAsStream(resourceName));
        CacheHolder.CACHE.store(key, props);
        return props;
    }

    public static @NotNull PGProperties getSharedInstanceForNamedResource(@NotNull @NonNls String resourceName) {
        return getSharedInstanceForNamedResource(resourceName.startsWith("/") ? resourceName : ("/" + resourceName), PGProperties.class);
    }

    private static final class CacheHolder {
        private static final ObjCache CACHE = new ObjCache();
    }
}
