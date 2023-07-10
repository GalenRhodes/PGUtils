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

import com.projectgalen.lib.utils.*;
import com.projectgalen.lib.utils.errors.Errors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.projectgalen.lib.utils.PGArrays.areEqual;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public final class Reflection {
    private static final PGResourceBundle msgs  = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.pg_messages");
    private static final PGProperties     props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class);

    public static final Class<?>[] NO_TYPES = {};

    private Reflection() { }

    public static @Nullable Object callMethod(@NotNull Object obj, @NotNull String methodName, Class<?> @NotNull [] parameterTypes, Object @NotNull ... parameters) {
        try { return callMethod(obj.getClass().getMethod(methodName, parameterTypes), obj, parameters); } catch(Exception e) { throw Errors.makeRuntimeException(e); }
    }

    public static Object callMethod(@NotNull Method method, @Nullable Object obj, Object @NotNull ... parameters) {
        try {
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if((obj == null) && !isStatic) throw new NullPointerException();
            if(isStatic && (obj != null)) throw new IllegalArgumentException(msgs.getString("msg.err.reflect.non_null_instance_object_to_static_method"));
            if(!method.canAccess(obj)) method.setAccessible(true);
            return method.invoke(obj, parameters);
        }
        catch(Exception e) { throw Errors.makeRuntimeException(e); }
    }

    public static @Nullable Object callStaticMethod(@NotNull String className, @NotNull String methodName, Class<?> @NotNull [] parameterTypes, Object @NotNull ... parameters) {
        try { return callMethod(Class.forName(className).getMethod(methodName, parameterTypes), null, parameters); } catch(Exception e) { throw Errors.makeRuntimeException(e); }
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

    public static boolean doTypesMatch(boolean exact, Class<?> @NotNull [] aTypes, Class<?> @NotNull [] bTypes) {
        return areEqual(aTypes, bTypes, (o1, o2) -> isTypeMatch(exact, o1, o2));
    }

    public static @NotNull List<Method> findGetters(@NotNull Class<?> cls) {
        return Reflection2.getMethods(cls).filter(m -> ((m.getReturnType() != Void.class) && (m.getParameterCount() == 0))).collect(Collectors.toList());
    }

    public static @NotNull List<Method> findSetters(@NotNull Class<?> cls) {
        return findSetterStream(cls).collect(Collectors.toList());
    }

    public static @NotNull List<Method> findSettersForTypes(@NotNull Class<?> cls, @NotNull Class<?>... paramTypes) {
        return findSettersForTypes(cls, false, paramTypes);
    }

    public static @NotNull List<Method> findSettersForTypes(@NotNull Class<?> cls, boolean exact, @NotNull Class<?> @NotNull ... parameterTypes) {
        return ((parameterTypes.length == 0) ? findSetters(cls) : findSetterStream(cls).filter(m -> isAnyType(exact, m.getParameterTypes()[0], parameterTypes)).collect(Collectors.toList()));
    }

    public static void forEachField(@NotNull Class<?> cls, @NotNull Function<Field, Boolean> delegate) {
        AtomicBoolean stop = new AtomicBoolean(false);
        Reflection2.getFields(cls, true).forEach(f -> { if(!stop.get()) stop.set(delegate.apply(f)); });
    }

    public static void forEachMethod(@NotNull Class<?> cls, @NotNull Function<Method, Boolean> delegate) {
        AtomicBoolean stop = new AtomicBoolean(false);
        Reflection2.getMethods(cls, true).forEach(m -> { if(!stop.get()) stop.set(delegate.apply(m)); });
    }

    public static void forEachSuperclass(@NotNull Class<?> cls, @NotNull Function<Class<?>, Boolean> delegate) {
        AtomicBoolean stop = new AtomicBoolean();
        Reflection2.getClassHierarchy(cls).forEachOrdered(c -> { if(!stop.get()) stop.set(delegate.apply(c)); });
    }

    public static @NotNull Field getAccessibleField(@NotNull Class<?> cls, @NotNull String name) {
        return makeAccessable(getField(cls, name));
    }

    public static @Nullable Field getAccessibleFieldOrNull(@NotNull Class<?> cls, @NotNull String name) {
        return makeAccessable(getFieldOrNull(cls, name));
    }

    public static @NotNull Method getAccessibleMethod(@NotNull Class<?> cls, @NotNull String name, @NotNull Class<?>... parameterTypes) throws NoSuchMethodException {
        return makeAccessable(getMethod(cls, name, parameterTypes));
    }

    public static @Nullable Method getAccessibleMethodOrNull(@NotNull Class<?> cls, @NotNull String name, @NotNull Class<?>... parameterTypes) {
        return makeAccessable(getMethodOrNull(cls, name, parameterTypes));
    }

    public static @Nullable <T extends Annotation> T getAnnotation(@NotNull AnnotatedElement element, @NotNull Class<T> annotationClass) {
        return Stream.of(element.getAnnotationsByType(annotationClass)).findFirst().orElse(null);
    }

    public static @NotNull Field getField(@NotNull Class<?> cls, @NotNull String name) {
        return Reflection2.getFields(cls, true).filter(f -> f.getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public static @Nullable Field getFieldOrNull(@NotNull Class<?> cls, @NotNull String name) {
        return Reflection2.getFields(cls, true).filter(f -> f.getName().equals(name)).findFirst().orElse(null);
    }

    public static @NotNull TypeInfo getFieldTypeInfo(@NotNull Field field) {
        return new TypeInfo(field.getGenericType());
    }

    public static @Nullable Object getFieldValue(@NotNull Field field, @Nullable Object obj) {
        try {
            return makeAccessable(field).get(obj);
        }
        catch(Exception e) {
            throw getError(e, "msg.err.reflect.get_fld_val_failed", field.getName(), ((obj == null) ? field.getDeclaringClass() : obj.getClass()).getName(), e);
        }
    }

    @SafeVarargs
    public static @NotNull List<Field> getFieldsWithAllAnnotations(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        return Reflection2.getFields(cls).filter(f -> hasAllAnnotations(f, annotationClasses)).collect(Collectors.toList());
    }

    @SafeVarargs
    public static @NotNull List<Field> getFieldsWithAnyAnnotation(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        return Reflection2.getFields(cls).filter(f -> hasAnyAnnotation(f, annotationClasses)).collect(Collectors.toList());
    }

    /**
     * This method functions like {@link Class#getMethod(String, Class...)} except that it will also search private, protected, and package member methods as well as public member methods. In this
     * respect it behaves similar to {@link Class#getDeclaredMethod(String, Class...)} except that it also looks into super-classes. Parameter types will not be matched exactly.
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
        return getMethod(cls, name, false, parameterTypes);
    }

    /**
     * This method functions like {@link Class#getMethod(String, Class...)} except that it will also search private, protected, and package member methods as well as public member methods. In this
     * respect it behaves similar to {@link Class#getDeclaredMethod(String, Class...)} except that it also looks into super-classes.
     *
     * @param cls            The class that will be searched for the specified method.
     * @param name           The name of the method.
     * @param exactTypeMatch If <code>true</code> then the parameter types provided must match the parameter types of the method exactly. Otherwise assignment compatability will also be used to
     *                       determine if there is a match.
     * @param parameterTypes The parameter types.
     *
     * @return The method.
     *
     * @throws NoSuchMethodException If the method cannot be found.
     */
    public static @NotNull Method getMethod(@NotNull Class<?> cls, @NotNull String name, boolean exactTypeMatch, @NotNull Class<?>... parameterTypes) throws NoSuchMethodException {
        return Null.requireOrThrow(getMethodOrNull(cls, name, exactTypeMatch, parameterTypes),
                                   () -> new NoSuchMethodException(props.format("reflect.nosuchmethod.msg.format", cls.getName(), name, getClassNames(parameterTypes))));
    }

    /**
     * This method is exactly like {@link Reflection#getMethod(Class, String, Class[])} except that instead of throwing a NoSuchMethodException exception if the method is not found it simply returns
     * null. Parameter types will not be matched exactly.
     *
     * @param cls            The class that will be searched for the specified method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types.
     *
     * @return The method.
     */
    public static @Nullable Method getMethodOrNull(@NotNull Class<?> cls, @NotNull String name, @NotNull Class<?>... parameterTypes) {
        return getMethodOrNull(cls, name, false, parameterTypes);
    }

    /**
     * This method is exactly like {@link Reflection#getMethod(Class, String, Class[])} except that instead of throwing a NoSuchMethodException exception if the method is not found it simply returns
     * null.
     *
     * @param cls            The class that will be searched for the specified method.
     * @param name           The name of the method.
     * @param exactTypeMatch If <code>true</code> then the parameter types provided must match the parameter types of the method exactly. Otherwise assignment compatability will also be used to
     *                       determine if there is a match.
     * @param parameterTypes The parameter types.
     *
     * @return The method.
     */
    public static @Nullable Method getMethodOrNull(@NotNull Class<?> cls, @NotNull String name, boolean exactTypeMatch, @NotNull Class<?>... parameterTypes) {
        return Reflection2.getMethods(cls).filter(m -> (m.getName().equals(name) && doTypesMatch(exactTypeMatch, m.getParameterTypes(), parameterTypes))).findFirst().orElse(null);
    }

    @NotNull
    @SafeVarargs
    public static List<Method> getMethodsWithAllAnnotations(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        return Reflection2.getMethods(cls).filter(m -> hasAllAnnotations(m, annotationClasses)).collect(Collectors.toList());
    }

    @NotNull
    @SafeVarargs
    public static List<Method> getMethodsWithAnyAnnotation(@NotNull Class<?> cls, @NotNull Class<? extends Annotation>... annotationClasses) {
        return Reflection2.getMethods(cls).filter(m -> hasAnyAnnotation(m, annotationClasses)).collect(Collectors.toList());
    }

    public static @NotNull Stream<Method> getNestedDeclaredMethodStream(@NotNull Class<?> cls) {
        return Stream.of(cls.getDeclaredMethods());
    }

    public static @NotNull List<TypeInfo> getParameterTypeInfo(@NotNull Method method) {
        return Stream.of(method.getGenericParameterTypes()).map(TypeInfo::new).collect(Collectors.toList());
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
        return Stream.of(annotationClasses).allMatch(element::isAnnotationPresent);
    }

    @SafeVarargs
    public static boolean hasAnyAnnotation(@NotNull AnnotatedElement element, @NotNull Class<? extends Annotation> @NotNull ... annotationClasses) {
        return Stream.of(annotationClasses).anyMatch(element::isAnnotationPresent);
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
        return isNumAssign(objectClassForPrimitive(leftHandClass), objectClassForPrimitive(rightHandClass));
    }

    /**
     * Tests to see if the two classes are a match. If <code>exactTypeMatch</code> is <code>true</code> then the two classes must be exactly the same. If <code>exactTypeMatch</code> is
     * <code>false</code> then if the classes are not exactly the same then a check will be made to see if the <code>left</code> class is assignable from the <code>right</code> class. If they both
     * represent numbers (primitive or subclasses of java.lang.Number) then a check will be made to see if <code>right</code> can be assigned to <code>left</code> through standard sign extension.
     *
     * @param exactTypeMatch <code>true</code> if the classes must match exaclty.
     * @param left           the left class.
     * @param right          the right class.
     *
     * @return <code>true</code> if they match.
     */
    public static boolean isTypeMatch(boolean exactTypeMatch, @NotNull Class<?> left, @NotNull Class<?> right) {
        return ((right == left) || (!exactTypeMatch && (left.isAssignableFrom(right) || isNumericallyAssignable(left, right) || isBooleanMismatch(left, right))));
    }

    public static <T extends AccessibleObject> T makeAccessable(T accObj) {
        if(accObj != null) accObj.setAccessible(true);
        return accObj;
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
        try { makeAccessable(field).set(obj, val); }
        catch(Exception e) { throw getError(e, "msg.err.reflect.set_fld_val_failed", field.getType(), ((obj == null) ? field.getDeclaringClass() : obj.getClass()).getName(), e); }
    }

    private static @NotNull Stream<Method> findSetterStream(@NotNull Class<?> cls) {
        return Reflection2.getMethods(cls).filter(m -> ((m.getReturnType() == Void.TYPE) && (m.getParameterCount() == 1) && m.getName().startsWith("set")));
    }

    private static @NotNull List<Type> getActualTypeArguments(@NotNull Type type) {
        return ((type instanceof ParameterizedType) ? Stream.of(((ParameterizedType)type).getActualTypeArguments()).collect(Collectors.toList()) : Collections.emptyList());
    }

    private static @NotNull String getClassNames(Class<?> @NotNull [] classes) {
        return U.join(", ", Stream.of(classes).map(Class::getName).toArray());
    }

    private static @NotNull RuntimeException getError(@NotNull Exception e, @NotNull String key, Object @NotNull ... args) {
        return new RuntimeException(msgs.format(key, args), e);
    }

    @Contract(pure = true)
    private static boolean isAnyType(@NotNull Class<?> cls, @NotNull Class<?> @NotNull ... others) {
        return isAnyType(true, cls, others);
    }

    private static boolean isAnyType(boolean exact, @NotNull Class<?> cls, @NotNull Class<?> @NotNull ... others) {
        return Stream.of(others).anyMatch(r -> isTypeMatch(exact, cls, r));
    }

    private static boolean isNumAssign(@NotNull Class<?> l, @NotNull Class<?> r) {
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
}
