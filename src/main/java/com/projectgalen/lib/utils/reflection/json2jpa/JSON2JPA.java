package com.projectgalen.lib.utils.reflection.json2jpa;

import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.annotations.PGDoppelganger;
import com.projectgalen.lib.utils.reflection.Reflection;
import com.projectgalen.lib.utils.reflection.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings({ "unchecked" })
public class JSON2JPA {

    private static final PGResourceBundle msgs = PGResourceBundle.getSharedBundle("com.projectgalen.lib.utils.pg_messages");

    private JSON2JPA() {
    }

    public static @NotNull <T> T convert(@NotNull Object source) {
        return (T)convert(null, source, new TreeMap<>());
    }

    public static @NotNull <T> T convert(@NotNull T target, @NotNull Object source) {
        return (T)convert(target, source, new TreeMap<>());
    }

    private static @NotNull Object convert(@Nullable Object tgt, @NotNull Object src, @NotNull Map<String, Object> cache) {
        TargetInfo tgtInfo = new TargetInfo(tgt, src, cache);
        if(tgtInfo.isCachedValue) return tgtInfo.target;

        Class<?> srcCls = src.getClass();
        Class<?> tgtCls = tgtInfo.target.getClass();

        try {
            for(String fld : tgtInfo.fieldNames) {
                Field tgtFld = Reflection.getAccessibleField(tgtCls, fld);
                Field srcFld = Reflection.getAccessibleField(srcCls, fld);
                copyFieldValue(tgtFld, tgtInfo.target, srcFld, src, cache);
            }
            return tgtInfo.target;
        }
        catch(RuntimeException e) {
            throw e;
        }
        catch(Exception e) {
            throw new RuntimeException(msgs.format("msg.err.json2jpa.xlate_doppelganger_failed", srcCls.getName(), tgtCls.getName()), e);
        }
    }

    private static void copyFieldValue(@NotNull Field tgtFld, @NotNull Object tgt, @NotNull Field srcFld, @NotNull Object src, @NotNull Map<String, Object> cache) {
        @Nullable Object srcVal = null;
        @Nullable Object tgtVal = null;
        try {
            srcVal = srcFld.get(src);
        }
        catch(Exception e) {
            throw new RuntimeException(msgs.format("msg.err.json2jpa.get_fld_val_failed", srcFld.getName(), src.getClass().getName(), e), e);
        }
        try {
            tgtVal = translate(new TypeInfo(tgtFld), new TypeInfo(srcFld), srcVal, cache);
        }
        catch(Exception e) {
            throw new RuntimeException(msgs.format("msg.err.json2jpa.xlate_value_failed", srcFld.getType().getName(), tgtFld.getType().getName(), e), e);
        }
        try {
            tgtFld.set(tgt, tgtVal);
        }
        catch(Exception e) {
            throw new RuntimeException(msgs.format("msg.err.json2jpa.set_fld_val_failed", tgtFld.getType(), tgt.getClass().getName(), e), e);
        }
    }

    private static boolean isTranslatableArray(@NotNull Class<?> tgtCls, @NotNull Class<?> srcCls) {
        return srcCls.isArray() && tgtCls.isArray() && !srcCls.getComponentType().isPrimitive() && !tgtCls.getComponentType().isPrimitive();
    }

    private static boolean isTranslatableList(@NotNull TypeInfo tgtTp, @NotNull TypeInfo srcTp, @NotNull Object srcVal) throws ClassNotFoundException {
        return ((srcVal instanceof List) &&
                List.class.isAssignableFrom(tgtTp.getTypeClass()) &&
                srcTp.isParameterizedType &&
                tgtTp.isParameterizedType &&
                (srcTp.argTypes.size() == 1) &&
                (tgtTp.argTypes.size() == 1));
    }

    private static Object translate(@NotNull TypeInfo tgtTp, @NotNull TypeInfo srcTp, @Nullable Object srcVal, @NotNull Map<String, Object> cache) throws Exception {
        Class<?> tgtCls = tgtTp.getTypeClass();
        Class<?> srcCls = srcTp.getTypeClass();

        if(srcVal == null) return null;

        if(tgtTp.equals(srcTp)) return ((srcVal instanceof List) ? new ArrayList<Object>((List<?>)srcVal) : srcVal);

        if(srcCls.isAnnotationPresent(PGDoppelganger.class) && tgtCls.isAnnotationPresent(PGDoppelganger.class)) return convert(null, srcVal, cache);

        if(isTranslatableList(tgtTp, srcTp, srcVal)) return translateList(tgtTp, srcTp, (List<?>)srcVal, cache);

        if(isTranslatableArray(tgtCls, srcCls)) return translateArray(tgtCls, srcCls, srcVal, cache);

        return srcVal;
    }

    @NotNull private static Object translateArray(@NotNull Class<?> tgtCls, @NotNull Class<?> srcCls, @NotNull Object srcArr, @NotNull Map<String, Object> cache) throws Exception {
        TypeInfo tgtCompTp = new TypeInfo(tgtCls.getComponentType());
        TypeInfo srcCompTp = new TypeInfo(srcCls.getComponentType());
        int      arrLn     = Array.getLength(srcArr);
        Object   arr       = Array.newInstance(tgtCls, arrLn);

        for(int i = 0; i < arrLn; i++) {
            Object srcElemVal = Array.get(srcArr, i);
            Object tgtElemVal = translate(tgtCompTp, srcCompTp, srcElemVal, cache);
            Array.set(arr, i, tgtElemVal);
        }

        return arr;
    }

    private static @NotNull List<Object> translateList(@NotNull TypeInfo tTp, @NotNull TypeInfo sTp, @NotNull List<?> srcLst, @NotNull Map<String, Object> cache) throws Exception {
        List<Object> list           = new ArrayList<>();
        TypeInfo     targetCompType = tTp.argTypes.get(0);
        TypeInfo     sourceCompType = sTp.argTypes.get(0);
        for(Object o : srcLst) list.add(translate(targetCompType, sourceCompType, o, cache));
        return list;
    }
}
