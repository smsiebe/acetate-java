/*
 * Copyright 2016 geoint.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.acetate.java.model.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.geoint.acetate.NotDomainResourceException;
import org.geoint.acetate.java.model.EventClass;
import org.geoint.acetate.java.model.ResourceClass;
import org.geoint.acetate.java.model.TypeClass;
import org.geoint.acetate.java.model.ValueClass;
import org.geoint.acetate.model.InvalidModelException;

/**
 * Reflection-based utility methods to determine domain modeling defined by java
 * classes.
 *
 * @author steve_siebert
 */
public class DomainReflectionUtil {

    public static <T> Optional<? extends TypeClass<T>> model(Class<T> clazz) {
        Optional<? extends TypeClass<T>> model = modelResource(clazz);
        if (model.isPresent()) {
            return model;
        }
        model = modelEvent(clazz);
        if (model.isPresent()) {
            return model;
        }
        return modelValue(clazz);
    }

    public static <T> Optional<ResourceClass<T>> modelResource(Class<T> clazz) {
        try {
            return Optional.of(ReflectedResourceClass.forClass(clazz));
        } catch (InvalidModelException ex) {
            return Optional.empty();
        }
    }

    public static <T> Optional<EventClass<T>> modelEvent(Class<T> clazz) {
        try {
            return Optional.of(ReflectedEventClass.forClass(clazz));
        } catch (InvalidModelException ex) {
            return Optional.empty();
        }
    }

    public static <T> Optional<ValueClass<T>> modelValue(Class<T> clazz) {
        try {
            return Optional.of(ReflectedValueClass.forClass(clazz));
        } catch (InvalidModelException ex) {
            return Optional.empty();
        }
    }

    /**
     * Indicates if the type is a array, a subclass of java.util.Collection, or
     * a subclass of java.util.Map.
     *
     * @param type type to check if the type may contain multiple object
     * instances
     * @return true if is a "collection" container
     */
    public static boolean isMulti(Type type) {
        return isArray(type)
                || isCollection(type)
                || isMap(type);
    }

    /**
     * Check if the type is a subclass of java.util.Map.
     *
     * @param type type to check
     * @return true if the type is a subclass of java.util.Map
     */
    public static boolean isMap(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return (type instanceof Class
                && Map.class.isAssignableFrom((Class) type));
    }

    /**
     * Check if the type is an array.
     *
     * @param type type to check
     * @return true if the type describes an array
     */
    public static boolean isArray(Type type) {
        return type instanceof GenericArrayType
                || (type instanceof Class && ((Class) type).isArray());
    }

    /**
     * Check if the type is a subclass of java.util.Collection.
     *
     * @param type type to check
     * @return true if the type is a subclass of java.util.Collection
     */
    public static boolean isCollection(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return type instanceof Class
                && Collection.class.isAssignableFrom((Class) type);

    }

    /**
     * Return the class defined by the java.util.collection generic parameter.
     *
     * @param type collection type
     * @return class defined by the generic parameter
     * @throws InvalidModelException thrown if not a collection or does not
     * define a generic type
     */
    public static Class<?> getCollectionClass(Type type)
            throws InvalidModelException {
        if (type instanceof ParameterizedType) {
            try {
                return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new InvalidModelException(String.format("Collection '%s' does "
                        + "not appear to defined a generic type.",
                        type.toString()), ex);
            }
        }
        throw new InvalidModelException(String.format("Cannot determine domain "
                + "type of collection defined by type '%s'.", type.toString()));
    }

    /**
     * Return the class defined by the generic array.
     *
     * @param arrayType array type
     * @return type contained by the array
     * @throws InvalidModelException thrown if the type does not define a type
     */
    public static Class<?> getArrayClass(Type arrayType)
            throws InvalidModelException {
        if (arrayType instanceof GenericArrayType) {
            return (Class) ((GenericArrayType) arrayType).getGenericComponentType();
        } else if (arrayType instanceof Class) {
            Class<?> clazz = (Class) arrayType;
            if (clazz.isArray()) {
                return clazz.getComponentType();
            }
        }
        throw new InvalidModelException(String.format("Cannot determine domain "
                + "domain type of array type '%s'.", arrayType.toString()));
    }

    /**
     * Return the class defined by the map generic key parameter.
     *
     * @param type map type
     * @return class defined by the map generic parameter for the key
     * @throws InvalidModelException if the type does not define a key generic
     * parameter
     *
     */
    public static Class<?> getMapKeyClass(Type type)
            throws InvalidModelException {
        if (!(type instanceof ParameterizedType)
                || ((ParameterizedType) type).getActualTypeArguments().length != 2) {
            throw new InvalidModelException(String.format("Type '%s' does not"
                    + "defined generic parameters for a map.", type.toString()));
        }
        return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    /**
     * Return the class defined by the map generic value parameter.
     *
     * @param type map type
     * @return class defined by the generic parameter for the value
     * @throws InvalidModelException if the type does not define a value generic
     * parameter
     */
    public static Class<?> getMapValueClass(Type type)
            throws InvalidModelException {
        if (!(type instanceof ParameterizedType)
                || ((ParameterizedType) type).getActualTypeArguments().length != 2) {
            throw new RuntimeException();
        }
        return (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
    }
}
