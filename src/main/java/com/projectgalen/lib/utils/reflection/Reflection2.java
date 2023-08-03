package com.projectgalen.lib.utils.reflection;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: Reflection2.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: June 29, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
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

import com.projectgalen.lib.utils.refs.ObjectRef;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public final class Reflection2 extends Reflection {

    Reflection2() { }

    @SafeVarargs public static @NotNull Stream<Field> getAnnotatedFields(@NotNull Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return getAnnotatedFields(clazz, true, annotationClasses);
    }

    @SafeVarargs public static @NotNull Stream<Field> getAnnotatedFields(@NotNull Class<?> clazz, boolean lookInSuper, Class<? extends Annotation>... annotationClasses) {
        return getFields(clazz, true).filter(f -> hasAnyAnnotation(f, annotationClasses));
    }

    @SafeVarargs public static @NotNull Stream<Method> getAnnotatedMethods(@NotNull Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return getAnnotatedMethods(clazz, true, annotationClasses);
    }

    @SafeVarargs public static @NotNull Stream<Method> getAnnotatedMethods(@NotNull Class<?> clazz, boolean lookInSuper, Class<? extends Annotation>... annotationClasses) {
        return getMethods(clazz, true).filter(f -> hasAnyAnnotation(f, annotationClasses));
    }

    public static @NotNull Stream<Class<?>> getClassHierarchy(@NotNull Class<?> clazz) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<>() {
            final ObjectRef<Class<?>> clsRef = new ObjectRef<>(clazz);

            public @Override boolean hasNext() {
                return (clsRef.value != null);
            }

            public @Override Class<?> next() {
                if(clsRef.value == null) throw new NoSuchElementException();
                Class<?> c = clsRef.value;
                clsRef.value = clsRef.value.getSuperclass();
                return c;
            }
        }, Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.NONNULL), false);
    }

    public static Field getField(@NotNull Class<?> clazz, @NotNull String name) {
        return getField(clazz, name, true);
    }

    public static Field getField(@NotNull Class<?> clazz, @NotNull String name, boolean lookInSuper) {
        return getFields(clazz, lookInSuper).filter(f -> f.getName().equals(name)).findFirst().orElse(null);
    }

    public static @NotNull Stream<Field> getFields(@NotNull Class<?> clazz, boolean lookInSuper) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new FieldIterator(clazz, lookInSuper), Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.NONNULL), false);
    }

    public static @NotNull Stream<Field> getFields(@NotNull Class<?> clazz) {
        return getFields(clazz, true);
    }

    public static <A extends Annotation> @NotNull Stream<AnnotatedReference<A, Field>> getFieldsWithAnnotation(@NotNull Class<?> clazz, Class<A> annotationClass) {
        return getFieldsWithAnnotation(clazz, annotationClass, true);
    }

    public static <A extends Annotation> @NotNull Stream<AnnotatedReference<A, Field>> getFieldsWithAnnotation(@NotNull Class<?> clazz, @NotNull Class<A> annotationClass, boolean lookInSuper) {
        return getFields(clazz, lookInSuper).filter(f -> f.isAnnotationPresent(annotationClass)).map(f -> new AnnotatedReference<>(f.getAnnotation(annotationClass), f));
    }

    public static @NotNull Stream<Method> getMethods(@NotNull Class<?> clazz, @NotNull String name, Class<?> @NotNull ... parameterClasses) {
        return getMethods(clazz, true, name, parameterClasses);
    }

    public static @NotNull Stream<Method> getMethods(@NotNull Class<?> clazz, boolean lookInSuper, @NotNull String name, Class<?> @NotNull ... parameterClasses) {
        return getMethods(clazz, lookInSuper).filter(m -> m.getName().equals(name) && Reflection.doTypesMatch(false, m.getParameterTypes(), parameterClasses));
    }

    public static @NotNull Stream<Method> getMethods(@NotNull Class<?> clazz, boolean lookInSuper) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new MethodIterator(clazz, lookInSuper), Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.NONNULL), false);
    }

    public static @NotNull Stream<Method> getMethods(@NotNull Class<?> clazz) {
        return getMethods(clazz, true);
    }

    public static <A extends Annotation> @NotNull Stream<AnnotatedReference<A, Method>> getMethodsWithAnnotation(@NotNull Class<?> clazz, Class<A> annotationClass) {
        return getMethodsWithAnnotation(clazz, annotationClass, true);
    }

    public static <A extends Annotation> @NotNull Stream<AnnotatedReference<A, Method>> getMethodsWithAnnotation(@NotNull Class<?> clazz, Class<A> annotationClass, boolean lookInSuper) {
        return getMethods(clazz, lookInSuper).filter(m -> m.isAnnotationPresent(annotationClass)).map(m -> new AnnotatedReference<>(m.getAnnotation(annotationClass), m));
    }

    private static final class FieldIterator implements Iterator<Field> {
        private       Class<?> clazz;
        private       Field[]  fields;
        private       int      idx;
        private final boolean  lookInSuper;

        public FieldIterator(@NotNull Class<?> clazz, boolean lookInSuper) {
            this.clazz       = clazz;
            this.idx         = 0;
            this.fields      = clazz.getDeclaredFields();
            this.lookInSuper = lookInSuper;
        }

        @Override public boolean hasNext() {
            while(clazz != null) {
                if(idx < fields.length) return true;
                clazz  = (lookInSuper ? clazz.getSuperclass() : null);
                idx    = 0;
                fields = ((clazz == null) ? new Field[0] : clazz.getDeclaredFields());
            }
            return false;
        }

        @Override public Field next() {
            if(hasNext()) return fields[idx++];
            throw new NoSuchElementException();
        }
    }

    private static final class MethodIterator implements Iterator<Method> {
        private       Class<?> clazz;
        private       Method[] methods;
        private       int      idx;
        private final boolean  lookInSuper;

        public MethodIterator(@NotNull Class<?> clazz, boolean lookInSuper) {
            this.clazz       = clazz;
            this.methods     = clazz.getDeclaredMethods();
            this.idx         = 0;
            this.lookInSuper = lookInSuper;
        }

        @Override public boolean hasNext() {
            while(clazz != null) {
                if(idx < methods.length) return true;
                clazz   = (lookInSuper ? clazz.getSuperclass() : null);
                idx     = 0;
                methods = ((clazz == null) ? new Method[0] : clazz.getDeclaredMethods());
            }
            return false;
        }

        @Override public Method next() {
            if(hasNext()) return methods[idx++];
            throw new NoSuchElementException();
        }
    }
}
