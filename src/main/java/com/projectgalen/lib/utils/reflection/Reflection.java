package com.projectgalen.lib.utils.reflection;

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.U;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reflection {
    private static final PGProperties props = PGProperties.getSharedInstanceForNamedResource("pg_properties.properties", PGProperties.class);

    private Reflection() {
    }

    public static @Nullable Object castIfNumeric(@Nullable Object value, @NotNull Class<?> targetClass) {
        if((value != null) && !targetClass.isAssignableFrom(value.getClass())) {
            if(value instanceof Character) value = (int)((Character)value);

            Class<?> l = objectClassForPrimitive(targetClass);
            Class<?> r = objectClassForPrimitive(value.getClass());

            if(Number.class.isAssignableFrom(targetClass) && (value instanceof Number)) {
                if(l == BigDecimal.class) return ((r == BigInteger.class) ? new BigDecimal((BigInteger)value) : BigDecimal.valueOf(((Number)value).doubleValue()));
                if((l == BigInteger.class) && !isAnyType(r, BigDecimal.class, Double.class, Float.class)) return BigInteger.valueOf(((Number)value).longValue());
                if((l == Double.class) && !isAnyType(r, BigDecimal.class, BigInteger.class)) return ((Number)value).doubleValue();
                if((l == Float.class) && !isAnyType(r, BigDecimal.class, BigInteger.class, Double.class)) return ((Number)value).floatValue();
                if((l == Long.class) && isAnyType(r, Integer.class, Short.class, Byte.class)) return ((Number)value).longValue();
                if((l == Integer.class) && isAnyType(r, Short.class, Byte.class)) return ((Number)value).intValue();
                if((l == Short.class) && (r == Byte.class)) return ((Number)value).shortValue();
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

    public static @NotNull List<Method> findSettersForTypes(@NotNull Class<?> cls, Class<?>... paramTypes) {
        return findSettersForTypes(cls, false, paramTypes);
    }

    public static @NotNull List<Method> findSettersForTypes(@NotNull Class<?> cls, boolean exact, Class<?>... pTypes) {
        List<Method> a      = new ArrayList<>();
        boolean      getAll = (pTypes.length == 0);
        forEach(cls, c -> {
            for(Method m : c.getDeclaredMethods()) {
                Class<?>[] mParamTypes = m.getParameterTypes();

                if((m.getReturnType() == void.class) && (mParamTypes.length == 1) && m.getName().startsWith("set")) {
                    Class<?> l = mParamTypes[0];
                    if(getAll) a.add(m);
                    else for(Class<?> r : pTypes) {
                        if(isTypeMatch(exact, l, r)) {
                            a.add(m);
                            break;
                        }
                    }
                }
            }
        });
        return a;
    }

    public static Field getAccessibleField(Class<?> cls, String name) throws NoSuchFieldException {
        Field field = getField(cls, name);
        field.setAccessible(true);
        return field;
    }

    public static Field getAccessibleFieldOrNull(Class<?> cls, String name) {
        Field field = getFieldOrNull(cls, name);
        if(field != null) field.setAccessible(true);
        return field;
    }

    public static @NotNull Method getAccessibleMethod(@NotNull Class<?> cls, @NotNull String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = getMethod(cls, name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    public static @Nullable Method getAccessibleMethodOrNull(@NotNull Class<?> cls, @NotNull String name, Class<?>... parameterTypes) {
        Method method = getMethodOrNull(cls, name, parameterTypes);
        if(method != null) method.setAccessible(true);
        return method;
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

    /**
     * This method functions like {@link Class#getMethod(String, Class...)} except that it will also search private, protected, and package member methods as well as public member
     * methods. In this respect it behaves similar to {@link Class#getDeclaredMethod(String, Class...)}.
     *
     * @param cls            The class that will be searched for the specified method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types.
     * @return The method.
     * @throws NoSuchMethodException If the method cannot be found.
     */
    public static @NotNull Method getMethod(@NotNull Class<?> cls, @NotNull String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = getMethodOrNull(cls, name, parameterTypes);
        if(method != null) return method;
        String msg = props.format("reflect.nosuchmethod.msg.format", cls.getName(), name, U.join(", ", U.translate(Object.class, Class::getName, parameterTypes)));
        throw new NoSuchMethodException(msg);
    }

    /**
     * This method is exactly like {@link Reflection#getMethod(Class, String, Class[])} except that instead of throwing a NoSuchMethodException exception if the method is not found
     * it simply returns null.
     *
     * @param cls            The class that will be searched for the specified method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types.
     * @return The method.
     */
    public static @Nullable Method getMethodOrNull(@NotNull Class<?> cls, @NotNull String name, Class<?>... parameterTypes) {
        Class<?> c = cls;
        while(c != null) try { return c.getDeclaredMethod(name, parameterTypes); } catch(NoSuchMethodException ignore) { c = c.getSuperclass(); }
        return null;
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

    public static @SafeVarargs boolean hasAllAnnotations(@NotNull AnnotatedElement annotatedElement, Class<? extends Annotation>... annotationClasses) {
        for(Class<? extends Annotation> ac : annotationClasses) if(!annotatedElement.isAnnotationPresent(ac)) return false;
        return true;
    }

    public static @SafeVarargs boolean hasAnyAnnotation(@NotNull AnnotatedElement annotatedElement, Class<? extends Annotation>... annotationClasses) {
        for(Class<? extends Annotation> ac : annotationClasses) if(annotatedElement.isAnnotationPresent(ac)) return true;
        return false;
    }

    public static boolean isBooleanMismatch(Class<?> aClass, Class<?> bClass) {
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

    private static boolean _isNumericallyAssignable(Class<?> l, Class<?> r) {
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

    private static void forEach(@NotNull Class<?> cls, @NotNull Foo001 lambda) {
        Class<?> _cls = cls;
        while(_cls != null) {
            lambda.action(_cls);
            _cls = _cls.getSuperclass();
        }
    }

    private static @NotNull List<Type> getActualTypeArguments(@NotNull Type type) {
        List<Type> list = new ArrayList<>();
        if(type instanceof ParameterizedType) list.addAll(Arrays.asList(((ParameterizedType)type).getActualTypeArguments()));
        return list;
    }

    private static boolean isAnyType(@NotNull Class<?> cls, Class<?>... others) {
        for(Class<?> oCls : others) if(cls == oCls) return true;
        return false;
    }

    private static boolean isTypeMatch(boolean exact, Class<?> l, Class<?> r) {
        return (r == l) || (!exact && (l.isAssignableFrom(r) || isNumericallyAssignable(l, r) || isBooleanMismatch(l, r)));
    }

    private static boolean isTypeMatches(boolean exact, Class<?> l, Class<?>... pTypes) {
        for(Class<?> r : pTypes) if(isTypeMatch(exact, l, r)) return true;
        return false;
    }

    private interface Foo001 {
        void action(@NotNull Class<?> cls);
    }
}
