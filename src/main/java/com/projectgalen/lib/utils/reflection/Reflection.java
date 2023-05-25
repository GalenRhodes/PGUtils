package com.projectgalen.lib.utils.reflection;
// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Reflection.java
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

import com.projectgalen.lib.utils.PGMath;
import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.U;
import com.projectgalen.lib.utils.delegates.GetWithValueDelegate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public final class Reflection {
    private static final PGResourceBundle msgs  = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);

    public static final Class<?>[] NO_TYPES = {};

    private Reflection() { }

    public static @Nullable Object callMethod(@NotNull Object obj, @NotNull String methodName, Class<?> @NotNull [] parameterTypes, Object @NotNull ... parameters) {
        try {
            return callMethod(obj.getClass().getMethod(methodName, parameterTypes), obj, parameters);
        }
        catch(Exception e) {
            if(e instanceof RuntimeException) throw (RuntimeException)e;
            throw new RuntimeException(e);
        }
    }

    public static Object callMethod(@NotNull Method method, @Nullable Object obj, Object @NotNull [] parameters) {
        try {
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if((obj == null) && !isStatic) throw new NullPointerException();
            if(isStatic && (obj != null)) throw new IllegalArgumentException(msgs.getString("msg.err.reflect.non_null_instance_object_to_static_method"));
            if(!method.canAccess(obj)) method.setAccessible(true);
            return method.invoke(obj, parameters);
        }
        catch(Exception e) {
            if(e instanceof RuntimeException) throw (RuntimeException)e;
            throw new RuntimeException(e);
        }
    }

    public static @Nullable Object callStaticMethod(@NotNull String className, @NotNull String methodName, Class<?> @NotNull [] parameterTypes, Object @NotNull ... parameters) {
        try {
            return callMethod(Class.forName(className).getMethod(methodName, parameterTypes), null, parameters);
        }
        catch(Exception e) {
            if(e instanceof RuntimeException) throw (RuntimeException)e;
            throw new RuntimeException(e);
        }
    }

    public static @Nullable Object castIfNumeric(@Nullable Object value, @NotNull Class<?> targetClass) {
        if((value != null) && !targetClass.isAssignableFrom(value.getClass())) {
            if(value instanceof Character) value = (int)((Character)value);

            if(Number.class.isAssignableFrom(targetClass) && (value instanceof Number)) {
                Class<?> l = objectClassForPrimitive(targetClass);
                if(l == BigDecimal.class) return PGMath.getBigDecimal((Number)value);
                if(l == BigInteger.class) return PGMath.getBigInteger((Number)value);
                if((l == Double.class) && !isAnyType(value.getClass(), BigDecimal.class, BigInteger.class)) return ((Number)value).doubleValue();
                if((l == Float.class) && !isAnyType(value.getClass(), BigDecimal.class, BigInteger.class, Double.class)) return ((Number)value).floatValue();
                if((l == Long.class) && isAnyType(value.getClass(), Integer.class, Short.class, Byte.class)) return ((Number)value).longValue();
                if((l == Integer.class) && isAnyType(value.getClass(), Short.class, Byte.class)) return ((Number)value).intValue();
                if((l == Short.class) && (value.getClass() == Byte.class)) return ((Number)value).shortValue();
            }
        }

        return value;
    }

    public static @NotNull List<Method> findGetters(@NotNull Class<?> cls) {
        List<Method> methods = new ArrayList<>();
        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) if((m.getReturnType() != Void.class) && (m.getParameterCount() == 0) && m.getName().startsWith("get")) methods.add(m);
            cls = cls.getSuperclass();
        }
        return methods;
    }

    public static @NotNull List<Method> findSetters(@Nullable Class<?> cls) {
        List<Method> methods = new ArrayList<>();
        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) if((m.getReturnType() == void.class) && (m.getParameterCount() == 1) && m.getName().startsWith("set")) methods.add(m);
            cls = cls.getSuperclass();
        }
        return methods;
    }

    public static @NotNull List<Method> findSettersForTypes(@NotNull Class<?> cls, @NotNull Class<?>... paramTypes) {
        return findSettersForTypes(cls, false, paramTypes);
    }

    public static @NotNull List<Method> findSettersForTypes(@NotNull Class<?> cls, boolean exact, @NotNull Class<?> @NotNull ... pTypes) {
        List<Method> a      = new ArrayList<>();
        boolean      getAll = (pTypes.length == 0);
        forEachSuperclass(cls, c -> {
            for(Method m : c.getDeclaredMethods()) {
                Class<?>[] mParamTypes = m.getParameterTypes();

                if((m.getReturnType() == void.class) && (mParamTypes.length == 1) && m.getName().startsWith("set")) {
                    Class<?> l = mParamTypes[0];
                    if(getAll) { a.add(m); }
                    else {
                        for(Class<?> r : pTypes) {
                            if(isTypeMatch(exact, l, r)) {
                                a.add(m);
                                break;
                            }
                        }
                    }
                }
            }
            return false;
        });
        return a;
    }

    public static void forEachField(@NotNull Class<?> cls, @NotNull GetWithValueDelegate<Field, Boolean> delegate) {
        forEachSuperclass(cls, c -> {
            for(Field field : c.getDeclaredFields()) {
                @Nullable Boolean obj = delegate.action(field);
                if(Objects.requireNonNullElse(obj, false)) return true;
            }
            return false;
        });
    }

    public static void forEachMethod(@NotNull Class<?> cls, @NotNull GetWithValueDelegate<Method, Boolean> delegate) {
        forEachSuperclass(cls, c -> {
            for(Method method : c.getDeclaredMethods()) {
                @Nullable Boolean obj = delegate.action(method);
                if(Objects.requireNonNullElse(obj, false)) return true;
            }
            return false;
        });
    }

    public static void forEachSuperclass(@NotNull Class<?> cls, @NotNull GetWithValueDelegate<Class<?>, Boolean> delegate) {
        Class<?> c = cls;
        while(c != null) {
            @Nullable Boolean obj = delegate.action(c);
            if(Objects.requireNonNullElse(obj, false)) break;
            c = c.getSuperclass();
        }
    }

    public static @NotNull Field getAccessibleField(@NotNull Class<?> cls, @NotNull String name) throws NoSuchFieldException {
        Field field = getField(cls, name);
        field.setAccessible(true);
        return field;
    }

    public static @Nullable Field getAccessibleFieldOrNull(@NotNull Class<?> cls, @NotNull String name) {
        Field field = getFieldOrNull(cls, name);
        if(field != null) field.setAccessible(true);
        return field;
    }

    public static @NotNull Method getAccessibleMethod(@NotNull Class<?> cls, @NotNull String name, @NotNull Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = getMethod(cls, name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    public static @Nullable Method getAccessibleMethodOrNull(@NotNull Class<?> cls, @NotNull String name, @NotNull Class<?>... parameterTypes) {
        Method method = getMethodOrNull(cls, name, parameterTypes);
        if(method != null) method.setAccessible(true);
        return method;
    }

    public static @Nullable <T extends Annotation> T getAnnotation(@NotNull AnnotatedElement element, @NotNull Class<T> annotationClass) {
        if(element.getClass() == Class.class) {
            Class<?> cls = (Class<?>)element;
            while(cls != null) {
                T a = cls.getAnnotation(annotationClass);
                if(a != null) return a;
                cls = cls.getSuperclass();
            }
            return null;
        }
        return element.getAnnotation(annotationClass);
    }

    public static @NotNull Field getField(@NotNull Class<?> cls, @NotNull String name) throws NoSuchFieldException {
        Field field = getFieldOrNull(cls, name);
        if(field == null) throw new NoSuchFieldException(name);
        return field;
    }

    public static @Nullable Field getFieldOrNull(@NotNull Class<?> cls, @NotNull String name) {
        Class<?> c = cls;
        while(c != null) { try { return cls.getDeclaredField(name); } catch(NoSuchFieldException e) { c = c.getSuperclass(); } }
        return null;
    }

    public static @NotNull TypeInfo getFieldTypeInfo(@NotNull Field field) {
        return new TypeInfo(field.getGenericType());
    }

    public static @Nullable Object getFieldValue(@NotNull Field field, @Nullable Object obj) {
        try {
            if(!field.canAccess(obj)) field.setAccessible(true);
            return field.get(obj);
        }
        catch(Exception e) {
            Class<?> cls = (obj == null) ? field.getDeclaringClass() : obj.getClass();
            throw new RuntimeException(msgs.format("msg.err.reflect.get_fld_val_failed", field.getName(), cls.getName(), e), e);
        }
    }

    @NotNull
    @SafeVarargs
    public static List<Field> getFieldsWithAllAnnotations(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        List<Field> fields = new ArrayList<>();
        while(cls != null) {
            for(Field f : cls.getDeclaredFields()) if(hasAllAnnotations(f, annotationClasses)) fields.add(f);
            cls = cls.getSuperclass();
        }
        return fields;
    }

    @NotNull
    @SafeVarargs
    public static List<Field> getFieldsWithAnyAnnotation(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        List<Field> fields = new ArrayList<>();
        while(cls != null) {
            for(Field f : cls.getDeclaredFields()) if(hasAnyAnnotation(f, annotationClasses)) fields.add(f);
            cls = cls.getSuperclass();
        }
        return fields;
    }

    /**
     * This method functions like {@link Class#getMethod(String, Class...)} except that it will also search private, protected, and package member methods as well as public member methods. In this
     * respect it behaves similar to {@link Class#getDeclaredMethod(String, Class...)}.
     *
     * @param cls            The class that will be searched for the specified method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types.
     *
     * @return The method.
     *
     * @throws NoSuchMethodException If the method cannot be found.
     */
    public static @NotNull Method getMethod(@NotNull Class<?> cls, @NotNull String name, @NotNull Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = getMethodOrNull(cls, name, parameterTypes);
        if(method != null) return method;
        String msg = props.format("reflect.nosuchmethod.msg.format", cls.getName(), name, U.join(", ", U.translate(Object.class, Class::getName, parameterTypes)));
        throw new NoSuchMethodException(msg);
    }

    /**
     * This method is exactly like {@link Reflection#getMethod(Class, String, Class[])} except that instead of throwing a NoSuchMethodException exception if the method is not found it simply returns
     * null.
     *
     * @param cls            The class that will be searched for the specified method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types.
     *
     * @return The method.
     */
    public static @Nullable Method getMethodOrNull(@NotNull Class<?> cls, @NotNull String name, @NotNull Class<?>... parameterTypes) {
        Class<?> c = cls;
        while(c != null) try { return c.getDeclaredMethod(name, parameterTypes); } catch(NoSuchMethodException ignore) { c = c.getSuperclass(); }
        return null;
    }

    @NotNull
    @SafeVarargs
    public static List<Method> getMethodsWithAllAnnotations(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        List<Method> methods = new ArrayList<>();
        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) if(hasAllAnnotations(m, annotationClasses)) methods.add(m);
            cls = cls.getSuperclass();
        }
        return methods;
    }

    @NotNull
    @SafeVarargs
    public static List<Method> getMethodsWithAnyAnnotation(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        List<Method> methods = new ArrayList<>();
        while(cls != null) {
            for(Method m : cls.getDeclaredMethods()) if(hasAnyAnnotation(m, annotationClasses)) methods.add(m);
            cls = cls.getSuperclass();
        }
        return methods;
    }

    public static @NotNull List<TypeInfo> getParameterTypeInfo(@NotNull Method method) {
        List<TypeInfo> list = new ArrayList<>();
        for(Type t : method.getGenericParameterTypes()) list.add(new TypeInfo(t));
        return list;
    }

    public static @NotNull List<Type> getParameterizedFieldTypes(@NotNull Field field) {
        return getActualTypeArguments(field.getGenericType());
    }

    public static @NotNull List<Type> getParameterizedParameterTypes(@NotNull Method method, int parameterIndex) {
        return getActualTypeArguments(method.getGenericParameterTypes()[parameterIndex]);
    }

    public static @NotNull List<Type> getParameterizedReturnTypes(@NotNull Method method) {
        return getActualTypeArguments(method.getGenericReturnType());
    }

    public static @NotNull TypeInfo getReturnTypeInfo(@NotNull Method method) {
        return new TypeInfo(method.getGenericReturnType());
    }

    @SafeVarargs
    public static boolean hasAllAnnotations(@NotNull AnnotatedElement element, @NotNull Class<? extends Annotation> @NotNull ... annotationClasses) {
        for(Class<? extends Annotation> ac : annotationClasses) if(!isAnnotationPresent(element, ac)) return false;
        return true;
    }

    @SafeVarargs
    public static boolean hasAnyAnnotation(@NotNull AnnotatedElement element, @NotNull Class<? extends Annotation> @NotNull ... annotationClasses) {
        for(Class<? extends Annotation> ac : annotationClasses) if(isAnnotationPresent(element, ac)) return true;
        return false;
    }

    public static boolean isAnnotationPresent(@NotNull AnnotatedElement element, @NotNull Class<? extends Annotation> annotationClass) {
        if(element.getClass() == Class.class) {
            AtomicBoolean found = new AtomicBoolean(false);
            forEachSuperclass((Class<?>)element, cls -> U.atomicSet(found, cls.isAnnotationPresent(annotationClass)));
            return found.get();
        }
        return element.isAnnotationPresent(annotationClass);
    }

    public static boolean isBooleanMismatch(@NotNull Class<?> aClass, @NotNull Class<?> bClass) {
        return ((bClass == boolean.class) && (aClass == Boolean.class)) || ((bClass == Boolean.class) && (aClass == boolean.class));
    }

    public static boolean isNumeric(@NotNull Class<?> cls) {
        return (isNumericPrimitive(cls) || isNumericObject(cls));
    }

    public static boolean isNumericObject(@NotNull Class<?> cls) {
        return (Number.class.isAssignableFrom(cls) || (cls == Character.class));
    }

    public static boolean isNumericPrimitive(@NotNull Class<?> cls) {
        return isAnyType(cls, byte.class, short.class, char.class, int.class, long.class, float.class, double.class);
    }

    public static boolean isNumericallyAssignable(@NotNull Class<?> leftHandClass, @NotNull Class<?> rightHandClass) {
        return _isNumericallyAssignable(objectClassForPrimitive(leftHandClass), objectClassForPrimitive(rightHandClass));
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

    public static void setFieldValue(@NotNull Field field, @Nullable Object obj, @Nullable Object val) {
        try {
            field.set(obj, val);
        }
        catch(Exception e) {
            Class<?> cls = ((obj == null) ? field.getDeclaringClass() : obj.getClass());
            throw new RuntimeException(msgs.format("msg.err.reflect.set_fld_val_failed", field.getType(), cls.getName(), e), e);
        }
    }

    private static boolean _isNumericallyAssignable(@NotNull Class<?> l, @NotNull Class<?> r) {
        if(!(isNumericObject(l) && isNumericObject(r))) return false;
        if((l == r) || (l == BigDecimal.class)) return true;
        if(l == BigInteger.class) return isAnyType(r, BigDecimal.class, Double.class, Float.class);
        if(l == Short.class) return (r == Byte.class);
        if(l == Character.class) return isAnyType(r, Short.class, Byte.class);
        if(l == Integer.class) return isAnyType(r, Character.class, Short.class, Byte.class);
        if(l == Long.class) return isAnyType(r, Integer.class, Character.class, Short.class, Byte.class);
        if(l == Float.class) return isAnyType(r, Long.class, Integer.class, Character.class, Short.class, Byte.class);
        if(l == Double.class) return isAnyType(r, Float.class, Long.class, Integer.class, Character.class, Short.class, Byte.class);
        return false;
    }

    private static @NotNull List<Type> getActualTypeArguments(@NotNull Type type) {
        List<Type> list = new ArrayList<>();
        if(type instanceof ParameterizedType) list.addAll(Arrays.asList(((ParameterizedType)type).getActualTypeArguments()));
        return list;
    }

    @Contract(pure = true)
    private static boolean isAnyType(@NotNull Class<?> cls, @NotNull Class<?> @NotNull ... others) {
        for(Class<?> oCls : others) if(cls == oCls) return true;
        return false;
    }

    private static boolean isTypeMatch(boolean exact, @NotNull Class<?> l, @NotNull Class<?> r) {
        return ((r == l) || (!exact && (l.isAssignableFrom(r) || isNumericallyAssignable(l, r) || isBooleanMismatch(l, r))));
    }

    private static boolean isTypeMatches(boolean exact, @NotNull Class<?> l, @NotNull Class<?> @NotNull ... pTypes) {
        for(Class<?> r : pTypes) if(isTypeMatch(exact, l, r)) return true;
        return false;
    }
}
