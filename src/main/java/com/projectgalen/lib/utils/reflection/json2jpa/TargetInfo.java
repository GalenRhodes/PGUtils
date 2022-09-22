package com.projectgalen.lib.utils.reflection.json2jpa;

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.annotations.PGDoppelganger;
import com.projectgalen.lib.utils.annotations.PGJPA;
import com.projectgalen.lib.utils.reflection.Reflection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
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
            fieldNames = dpplgr.fieldNames();

            Field    idFld  = Reflection.getAccessibleField(srcCls, dpplgr.idField());
            Object   idVal  = idFld.get(src);
            String   dClsNm = dpplgr.className();
            Class<?> dCls   = Class.forName(dClsNm); // The expected target class.
            String   key    = ((idVal == null) ? null : props.format("json2jpa.cache.key.format", dClsNm, idVal));

            if((tgt == null) && (key != null)) {
                tgt = cache.get(key);
            }
            if(tgt == null) {
                tgt = getTargetFromDao(dCls, idVal, idFld.getType());
                if(tgt == null) tgt = dCls.getConstructor().newInstance();
                if(!dCls.isAssignableFrom(tgt.getClass()))
                    throw new IllegalArgumentException(msgs.format("msg.err.json2jpa.invalid_target", tgt.getClass().getName(), dClsNm));

                isCachedValue = false;
                target        = tgt;
                cache.put(key, target);
            }
            else {
                isCachedValue = true;
                target        = tgt;
            }
        }
        catch(Exception e) {
            throw new RuntimeException(String.format(msgs.getString("msg.err.json2jpa.infer_tgt_cls_failed"), srcCls.getName(), e), e);
        }
    }

    private static Object getTargetFromDao(Class<?> tgtCls, Object idVal, Class<?> idTp) throws Exception {
        if((idVal == null) || !tgtCls.isAnnotationPresent(PGJPA.class)) return null;
        Class<?> daoClass = Class.forName(String.format("%s.dao.%sDao", tgtCls.getPackageName(), tgtCls.getSimpleName()));
        return Reflection.getAccessibleMethod(daoClass, props.getProperty("json2jpa.dao.get_method_name"), idTp).invoke(null, idVal);
    }
}
