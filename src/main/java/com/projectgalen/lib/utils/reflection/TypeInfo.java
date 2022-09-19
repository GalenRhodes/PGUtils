package com.projectgalen.lib.utils.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public Class<?> getTypeClass() throws ClassNotFoundException {
        switch(typeName) { //@f:0
            case "boolean": return boolean.class;
            case "char":    return char.class;
            case "byte":    return byte.class;
            case "short":   return short.class;
            case "int":     return int.class;
            case "long":    return long.class;
            case "float":   return float.class;
            case "double":  return double.class;
            default:        return Class.forName(typeName);
        } //@f:1
    }

    @Override public int hashCode() {
        return Objects.hash(isParameterizedType, typeName, argTypes);
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof TypeInfo)) return false;
        TypeInfo typeInfo = (TypeInfo)o;
        return isParameterizedType == typeInfo.isParameterizedType && typeName.equals(typeInfo.typeName) && argTypes.equals(typeInfo.argTypes);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(typeName);
        if(isParameterizedType) {
            boolean first = true;
            sb.append('<');
            for(TypeInfo typeInfo : argTypes) {
                if(first) first = false;
                else sb.append(", ");
                sb.append(typeInfo);
            }
            sb.append('>');
        }

        return sb.toString();
    }
}
