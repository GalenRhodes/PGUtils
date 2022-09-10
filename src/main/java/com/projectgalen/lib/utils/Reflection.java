package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Reflection {
    private Reflection() {
    }

    public static @NotNull List<Method> findGetters(@NotNull Class<?> cls) {
        List<Method> methods = new ArrayList<>();

        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) {
                if((m.getReturnType() != Void.class) && (m.getParameterCount() == 0) && m.getName().startsWith("get")) {
                    methods.add(m);
                }
            }
            cls = cls.getSuperclass();
        }

        return methods;
    }

    public static @NotNull List<Method> findSetters(@NotNull Class<?> cls) {
        List<Method> methods = new ArrayList<>();

        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) {
                if((m.getReturnType() == Void.class) && (m.getParameterCount() == 1) && m.getName().startsWith("set")) {
                    methods.add(m);
                }
            }
            cls = cls.getSuperclass();
        }

        return methods;
    }

    public static @NotNull List<Method> findSettersForType(@NotNull Class<?> cls, Class<?>... paramTypes) {
        return findSettersForType(cls, false, paramTypes);
    }

    public static @NotNull List<Method> findSettersForType(@NotNull Class<?> cls, boolean exact, Class<?>... paramTypes) {
        List<Method> _methods = findSetters(cls);
        if(paramTypes.length == 0) return _methods;
        List<Method> methods = new ArrayList<>();

        for(Method m : _methods) {
            for(Class<?> givenParamType : paramTypes) {
                Class<?> methodParamType = m.getParameterTypes()[0];

                if(givenParamType == methodParamType) {
                    methods.add(m);
                }
                else if(!exact) {
                    if(methodParamType.isAssignableFrom(givenParamType)) methods.add(m);
                    else if(isNumeric(methodParamType) && isNumeric(givenParamType) && isNumericallyAssignable(methodParamType, givenParamType)) methods.add(m);
                    else if(isBooleanMismatch(givenParamType, methodParamType)) methods.add(m);
                }
            }
        }

        return methods;
    }

    public static @NotNull @SafeVarargs List<Field> getFieldsWithAllAnnotations(@NotNull Class<?> cls, Class<? extends Annotation>... annotationClasses) {
        List<Field> fields = new ArrayList<>();
        while(cls != null) {
            for(Field f : cls.getDeclaredFields()) if(hasAllAnnotations(f, annotationClasses)) fields.add(f);
            cls = cls.getSuperclass();
        }
        return fields;
    }

    public static @NotNull @SafeVarargs List<Field> getFieldsWithAnyAnnotation(@NotNull Class<?> cls, Class<? extends Annotation>... annotationClasses) {
        List<Field> fields = new ArrayList<>();
        while(cls != null) {
            for(Field f : cls.getDeclaredFields()) if(hasAnyAnnotation(f, annotationClasses)) fields.add(f);
            cls = cls.getSuperclass();
        }
        return fields;
    }

    public static @NotNull @SafeVarargs List<Method> getMethodsWithAllAnnotations(@NotNull Class<?> cls, Class<? extends Annotation>... annotationClasses) {
        List<Method> methods = new ArrayList<>();
        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) if(hasAllAnnotations(m, annotationClasses)) methods.add(m);
            cls = cls.getSuperclass();
        }
        return methods;
    }

    public static @NotNull @SafeVarargs List<Method> getMethodsWithAnyAnnotation(@NotNull Class<?> cls, Class<? extends Annotation>... annotationClasses) {
        List<Method> methods = new ArrayList<>();
        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) if(hasAnyAnnotation(m, annotationClasses)) methods.add(m);
            cls = cls.getSuperclass();
        }
        return methods;
    }

    public static @SafeVarargs boolean hasAllAnnotations(@NotNull AnnotatedElement annotatedElement, Class<? extends Annotation>... annotationClasses) {
        for(Class<? extends Annotation> ac : annotationClasses) if(annotatedElement.getAnnotation(ac) == null) return false;
        return true;
    }

    public static @SafeVarargs boolean hasAnyAnnotation(@NotNull AnnotatedElement annotatedElement, Class<? extends Annotation>... annotationClasses) {
        for(Class<? extends Annotation> ac : annotationClasses) if(annotatedElement.getAnnotation(ac) != null) return true;
        return false;
    }

    public static boolean isBooleanMismatch(Class<?> aCls, Class<?> bCls) {
        return ((bCls == boolean.class) && (aCls == Boolean.class)) || ((bCls == Boolean.class) && (aCls == boolean.class));
    }

    public static boolean isNumeric(@NotNull Class<?> cls) {
        return (isNumericPrimitive(cls) || isNumericObject(cls));
    }

    public static boolean isNumericObject(@NotNull Class<?> cls) {
        return (Number.class.isAssignableFrom(cls) || (cls == Character.class));
    }

    public static boolean isNumericPrimitive(@NotNull Class<?> cls) {
        if(!cls.isPrimitive()) return false;
        Class<?>[] primitiveNumberTypes = { byte.class, short.class, char.class, int.class, long.class, float.class, double.class };
        for(Class<?> clsPrimitive : primitiveNumberTypes) if(clsPrimitive == cls) return true;
        return false;
    }

    public static boolean isNumericallyAssignable(@NotNull Class<?> leftHandClass, @NotNull Class<?> rightHandClass) {
        Class<?> l = objectClassForPrimitive(leftHandClass);
        Class<?> r = objectClassForPrimitive(rightHandClass);

        return _isNumericallyAssignable(l, r);
    }

    public static @NotNull Class<?> objectClassForPrimitive(@NotNull Class<?> cls) {
        if(!cls.isPrimitive()) return cls;
        if(cls == char.class) return Character.class;
        if(cls == byte.class) return Byte.class;
        if(cls == short.class) return Short.class;
        if(cls == int.class) return Integer.class;
        if(cls == long.class) return Long.class;
        if(cls == float.class) return Float.class;
        if(cls == double.class) return Double.class;
        return Boolean.class;
    }

    protected static boolean _isNumericallyAssignable(Class<?> l, Class<?> r) {
        return ((l != Boolean.class) && (r != Boolean.class) && (l.isAssignableFrom(r) || ((l == Short.class) ?
                                                                                           _xa(r) :
                                                                                           ((l == Character.class) ?
                                                                                            _xb(r) :
                                                                                            ((l == Integer.class) ?
                                                                                             _xc(r) :
                                                                                             ((l == Long.class) ?
                                                                                              _xd(r) :
                                                                                              ((l == Float.class) ?
                                                                                               _xe(r) :
                                                                                               ((l == Double.class) ?
                                                                                                _xf(r) :
                                                                                                ((l == BigInteger.class) ? _xe(r) : ((l == BigDecimal.class) && _xg(r)))))))))));
    }

    public static @Nullable Class<?> primitiveClassForObject(@NotNull Class<?> cls) {
        if(cls.isPrimitive()) return cls;
        if(cls == Character.class) return char.class;
        if(cls == Byte.class) return byte.class;
        if(cls == Short.class) return short.class;
        if(cls == Integer.class) return int.class;
        if(cls == Long.class) return long.class;
        if(cls == Float.class) return float.class;
        if(cls == Double.class) return double.class;
        if(cls == Boolean.class) return boolean.class;
        return null;
    }

    private static boolean _xa(Class<?> r) {
        return (r == Byte.class);
    }

    private static boolean _xb(Class<?> r) {
        return ((r == Short.class) || _xa(r));
    }

    private static boolean _xc(Class<?> r) {
        return ((r == Character.class) || _xb(r));
    }

    private static boolean _xd(Class<?> r) {
        return ((r == Integer.class) || _xc(r));
    }

    private static boolean _xe(Class<?> r) {
        return ((r == Long.class) || _xd(r));
    }

    private static boolean _xf(Class<?> r) {
        return ((r == Float.class) || _xe(r));
    }

    private static boolean _xg(Class<?> r) {
        return ((r == Double.class) || _xf(r) || (r == BigInteger.class));
    }
}
