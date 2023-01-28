package com.projectgalen.lib.utils.reflection.json2jpa;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: TargetInfo.java
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TargetInfo {
    private static final PGResourceBundle msgs  = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);

    public final Object   target;
    public final String[] fieldNames;
    public final boolean  isCachedValue;

    public TargetInfo(Object tgt, @NotNull Object src, @NotNull Map<String, Object> cache) {
        Class<?> srcCls = src.getClass();

        try {
            PGDoppelganger dpplgr = Reflection.getAnnotation(srcCls, PGDoppelganger.class);
            if(dpplgr == null) throw new IllegalArgumentException(msgs.format("msg.err.json2jpa.missing_doppelganger_annotation", srcCls.getName()));

            Field    idFld  = Reflection.getAccessibleField(srcCls, dpplgr.idField());
            Object   idVal  = idFld.get(src);
            String   dClsNm = dpplgr.className();
            Class<?> dCls   = Class.forName(dClsNm); // The expected target class.
            String   key    = ((idVal == null) ? null : props.format("json2jpa.cache.key.format", dClsNm, idVal));

            if(Reflection.isAnnotationPresent(dCls, PGJPA.class)) {
                fieldNames = getJpaFields(dCls);
            }
            else if(Reflection.isAnnotationPresent(dCls, PGJSON.class)) {
                fieldNames = getJsonFields(dCls);
            }
            else if(Reflection.isAnnotationPresent(dCls, JsonPropertyOrder.class)) {
                fieldNames = Objects.requireNonNull(Reflection.getAnnotation(dCls, JsonPropertyOrder.class)).value();
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

    @NotNull
    private static Object createTarget(@NotNull Class<?> dCls, @NotNull Field idFld, @Nullable Object idVal) throws Exception {
        Object tgt = getTargetFromDao(dCls, idVal, idFld.getType());
        if(tgt == null) tgt = dCls.getConstructor().newInstance();
        return tgt;
    }

    @NotNull
    private static String getDaoClassName(@NotNull Class<?> tgtCls) {
        return props.format("json2jpa.dao.classname_format", tgtCls.getPackageName(), tgtCls.getSimpleName());
    }

    @NotNull
    private static String[] getJpaFields(@NotNull Class<?> dCls) {
        List<Field> f = Reflection.getFieldsWithAnyAnnotation(dCls, Id.class, Column.class, ManyToOne.class, OneToOne.class, OneToMany.class, ManyToMany.class);
        return f.stream().map(Field::getName).toArray(String[]::new);
    }

    @NotNull
    private static String[] getJsonFields(Class<?> dCls) {
        List<String> names = new ArrayList<>();
        Reflection.forEachField(dCls, f -> {
            if(Reflection.isAnnotationPresent(f, JsonProperty.class)) names.add(f.getName());
            return false;
        });
        return names.toArray(new String[0]);
    }

    @Nullable
    private static Object getTargetFromDao(@NotNull Class<?> tgtCls, @Nullable Object idVal, @NotNull Class<?> idTp) throws Exception {
        if(Reflection.isAnnotationPresent(tgtCls, PGJPA.class)) {
            try {
                if(idVal == null) return tgtCls.getConstructor().newInstance();
                Class<?> daoCls = Class.forName(getDaoClassName(tgtCls));
                Object   obj    = Reflection.getAccessibleMethod(daoCls, props.getProperty("json2jpa.dao.get_instance_method_name")).invoke(null);
                return Reflection.getAccessibleMethod(daoCls, props.getProperty("json2jpa.dao.get_method_name"), idTp).invoke(obj, idVal);
            }
            catch(ClassNotFoundException e) {
                // We will treat this case as if no DAO support is provided in the using application and simply return null.
            }
        }
        return null;
    }
}
