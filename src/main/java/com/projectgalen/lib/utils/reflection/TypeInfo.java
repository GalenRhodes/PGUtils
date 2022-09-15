package com.projectgalen.lib.utils.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeInfo {
    public final          boolean        isParameterizedType;
    public final @NotNull String         typeName;
    public final @NotNull List<TypeInfo> argTypes;

    public TypeInfo(@NotNull Type type) {
        isParameterizedType = (type instanceof ParameterizedType);

        if(isParameterizedType) {
            ParameterizedType pType = (ParameterizedType)type;
            this.typeName = pType.getRawType().getTypeName();

            List<TypeInfo> argTypes = new ArrayList<>();
            for(Type _type : pType.getActualTypeArguments()) argTypes.add(new TypeInfo(_type));
            this.argTypes = Collections.unmodifiableList(argTypes);
        }
        else {
            this.typeName = type.getTypeName();
            this.argTypes = Collections.emptyList();
        }
    }
}
