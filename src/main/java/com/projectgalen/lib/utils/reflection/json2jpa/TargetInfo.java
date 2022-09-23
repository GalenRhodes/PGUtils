package com.projectgalen.lib.utils.reflection.json2jpa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.annotations.PGDoppelganger;
import com.projectgalen.lib.utils.annotations.PGJPA;
import com.projectgalen.lib.utils.annotations.PGJSON;
import com.projectgalen.lib.utils.reflection.Reflection;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TargetInfo {
    private static final PGResourceBundle msgs  = PGResourceBundle.getSharedBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props = PGProperties.getSharedInstanceForNamedResource("pg_properties.properties", PGProperties.class);

    public final Object   target;
    public final String[] fieldNames;
    public final boolean  isCachedValue;

    public TargetInfo(Object tgt, @NotNull Object src, @NotNull Map<String, Object> cache) {
        Class<?> srcCls = src.getClass();

        try {
            PGDoppelganger dpplgr = srcCls.getAnnotation(PGDoppelganger.class);
            if(dpplgr == null) throw new IllegalArgumentException(msgs.format("msg.err.json2jpa.missing_doppelganger_annotation", srcCls.getName()));

            Field    idFld  = Reflection.getAccessibleField(srcCls, dpplgr.idField());
            Object   idVal  = idFld.get(src);
            String   dClsNm = dpplgr.className();
            Class<?> dCls   = Class.forName(dClsNm); // The expected target class.
            String   key    = ((idVal == null) ? null : props.format("json2jpa.cache.key.format", dClsNm, idVal));

            if(dCls.isAnnotationPresent(PGJPA.class)) {
                fieldNames = getJpaFields(dCls);
            }
            else if(dCls.isAnnotationPresent(PGJSON.class)) {
                fieldNames = getJsonFields(dCls);
            }
            else if(dCls.isAnnotationPresent(JsonPropertyOrder.class)) {
                fieldNames = dCls.getAnnotation(JsonPropertyOrder.class).value();
            }
            else {
                fieldNames = dpplgr.fieldNames();
            }

            if((tgt == null) && (key != null)) {
                tgt = cache.get(key);
            }
            if(tgt == null) {
                target        = createTarget(dCls, idFld, idVal);
                isCachedValue = false;
                cache.put(key, target);
            }
            else if(dCls.isInstance(tgt)) {
                target        = tgt;
                isCachedValue = true;
            }
            else {
                throw new RuntimeException(String.format(msgs.getString("msg.err.json2jpa.target_object_incompatible"), tgt.getClass().getName(), dCls.getName()));
            }
        }
        catch(Exception e) {
            throw new RuntimeException(String.format(msgs.getString("msg.err.json2jpa.infer_tgt_cls_failed"), srcCls.getName(), e), e);
        }
    }

    private static @NotNull Object createTarget(@NotNull Class<?> dCls, @NotNull Field idFld, @Nullable Object idVal) throws Exception {
        Object tgt = getTargetFromDao(dCls, idVal, idFld.getType());
        if(tgt == null) tgt = dCls.getConstructor().newInstance();
        return tgt;
    }

    private static @NotNull String[] getJpaFields(@NotNull Class<?> dCls) {
        List<Field> f = Reflection.getFieldsWithAnyAnnotation(dCls, Id.class, Column.class, ManyToOne.class, OneToOne.class, OneToMany.class, ManyToMany.class);
        return f.stream().map(Field::getName).toArray(String[]::new);
    }

    private static @NotNull String[] getJsonFields(Class<?> dCls) {
        List<String> names = new ArrayList<>();
        Reflection.forEachField(dCls, f -> {
            if(f.isAnnotationPresent(JsonProperty.class)) names.add(f.getName());
            return false;
        });
        return names.toArray(new String[0]);
    }

    private static @Nullable Object getTargetFromDao(@NotNull Class<?> tgtCls, @Nullable Object idVal, @NotNull Class<?> idTp) throws Exception {
        if((idVal == null) || !tgtCls.isAnnotationPresent(PGJPA.class)) return null;
        try {
            Class<?> daoClass = Class.forName(String.format("%s.dao.%sDao", tgtCls.getPackageName(), tgtCls.getSimpleName()));
            Method   method1  = Reflection.getAccessibleMethod(daoClass, props.getProperty("json2jpa.dao.get_instance_method_name"));
            Method   method2  = Reflection.getAccessibleMethod(daoClass, props.getProperty("json2jpa.dao.get_method_name"), idTp);

            Object obj = method1.invoke(null);
            return method2.invoke(obj, idVal);
        }
        catch(ClassNotFoundException e) {
            // We will treat this case as if no DAO support is provided in the using application and simply return null.
            return null;
        }
    }
}
