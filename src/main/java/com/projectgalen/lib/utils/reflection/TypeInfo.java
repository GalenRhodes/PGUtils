package com.projectgalen.lib.utils.reflection;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: TypeInfo.java
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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TypeInfo implements Comparable<TypeInfo>, Serializable {
    public final          boolean        isParameterizedType;
    public final @NotNull String         typeName;
    public final @NotNull List<TypeInfo> argTypes;

    public TypeInfo(@NotNull Class<?> cls) {
        TypeVariable<? extends Class<?>>[] typeVars = cls.getTypeParameters();
        this.isParameterizedType = (typeVars.length > 0);
        this.typeName            = cls.getName();
        this.argTypes            = new ArrayList<>();
        for(TypeVariable<? extends Class<?>> tv : typeVars) this.argTypes.add(new TypeInfo(tv));
    }

    public TypeInfo(@NotNull Field field) {
        this(field.getGenericType());
    }

    /**
     * This constructor creates a {@link TypeInfo} object from the given method's return type. If you want the {@link TypeInfo} from the method's parameters then use the static method
     * {@link TypeInfo#getParameterTypeInfo(Method)}
     *
     * @param method The method to get the return {@link TypeInfo} from.
     */
    public TypeInfo(@NotNull Method method) {
        this(method.getGenericReturnType());
    }

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

    @Override
    public int compareTo(@NotNull TypeInfo o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        return ((this == o) || ((o instanceof TypeInfo) && _equals((TypeInfo)o)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(isParameterizedType, typeName, argTypes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(typeName);
        if(isParameterizedType) {
            boolean first = true;
            sb.append('<');
            for(TypeInfo typeInfo : argTypes) {
                if(first) { first = false; }
                else { sb.append(", "); }
                sb.append(typeInfo);
            }
            sb.append('>');
        }

        return sb.toString();
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

    private boolean _equals(@NotNull TypeInfo other) {
        return ((isParameterizedType == other.isParameterizedType) && typeName.equals(other.typeName) && argTypes.equals(other.argTypes));
    }

    public static @NotNull TypeInfo[] getParameterTypeInfo(@NotNull Method method) {
        List<TypeInfo> list = new ArrayList<>();
        for(Type pType : method.getGenericParameterTypes()) list.add(new TypeInfo(pType));
        return list.toArray(new TypeInfo[0]);
    }
}
