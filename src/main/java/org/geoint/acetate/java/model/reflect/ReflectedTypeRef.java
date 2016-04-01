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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import org.geoint.acetate.java.model.MapClassRef;
import org.geoint.acetate.java.model.TypeClass;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.java.model.TypeClassRef;
import org.geoint.acetate.model.NamedRef;

/**
 *
 * @author steve_siebert
 * @param <T> referenced class model
 */
public class ReflectedTypeRef<T extends TypeClass> implements TypeClassRef<T> {

    private final String name;
    private final Optional<String> description;
    private final T referencedModel;
    private final boolean multi;

    protected ReflectedTypeRef(String name, String description,
            T referencedModel, boolean multi) {
        this.name = name;
        this.description = Optional.ofNullable(
                (description == null || description.isEmpty())
                        ? null
                        : description);
        this.referencedModel = referencedModel;
        this.multi = multi;
    }

    /**
     * Provides a type reference for the methods return type.
     *
     * @param name reference name
     * @param desc reference description
     * @param m method to extract the return type from
     * @return reference to the method return type
     * @throws InvalidModelException if the provided method does not return a
     * domain type
     */
    public static NamedRef returnReference(String name,
            String desc, Method m) throws InvalidModelException {

        return typeReference(name, desc, m.getGenericReturnType());
    }

    public static NamedRef paramReference(String name,
            String desc, Parameter param) throws InvalidModelException {

        return typeReference(name, desc, param.getParameterizedType());
    }

    protected static NamedRef typeReference(
            String refName, String refDesc, Type type)
            throws InvalidModelException {
        final Class<?> refClass;

        boolean isCollection = DomainReflectionUtil.isCollection(type);
        if (isCollection) {
            if (DomainReflectionUtil.isArray(type)) {
                refClass = DomainReflectionUtil.getArrayClass(type);
            } else if (DomainReflectionUtil.isCollection(type)) {
                refClass = DomainReflectionUtil.getCollectionClass(type);
            } else if (DomainReflectionUtil.isMap(type)) {
                return mapReference(refName, refDesc, type);
            } else {
                throw new InvalidModelException(String.format("Unknown collection"
                        + "type '%s', unable to determine the domain type for "
                        + "reference '%s'",
                        type.toString(), refName));
            }
        } else if (type instanceof Class) {
            refClass = (Class) type;
        } else if (type instanceof ParameterizedType) {
            refClass = (Class) ((ParameterizedType) type).getRawType();
        } else {
            throw new InvalidModelException(String.format("Unknown type '%s', "
                    + "unable to determine the domain type for reference '%s'",
                    type.toString(), refName));
        }

        final ReflectedTypeClass<?> refModel
                = DomainClassReflector.model(refClass);

        return new ReflectedTypeRef(refName, refDesc, refModel, isCollection);
    }

    protected static MapClassRef<?, ?> mapReference(
            String refName, String refDesc, Type type)
            throws InvalidModelException {

        //TODO support annotation to override the key/value metadata
        NamedRef keyRef
                = ReflectedTypeRef.typeReference("key",
                        "",
                        DomainReflectionUtil.getMapKeyClass(type));
        if (!TypeClassRef.class.isAssignableFrom(keyRef.getClass())) {
            //probably another map, currently not supported
            //TODO add support for nested maps (probably not as a key...)
            throw new InvalidModelException(String.format("Map key must be a "
                    + "class representation of a domain type, not a '%s' type",
                    keyRef.getClass().getName()));
        }

        NamedRef valueRef
                = ReflectedTypeRef.typeReference("value",
                        "",
                        DomainReflectionUtil.getMapValueClass(type));
        if (!TypeClassRef.class.isAssignableFrom(valueRef.getClass())) {
            //probably another map, currently not supported
            //TODO add support for nested maps
            throw new InvalidModelException(String.format("Map value must be a "
                    + "class representation of a domain type, not a '%s' type",
                    valueRef.getClass().getName()));
        }

        return new MapClassRef(refName, refDesc,
                (TypeClassRef) keyRef, (TypeClassRef) valueRef);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public T getReferencedType() {
        return referencedModel;
    }

    public boolean isResource() {
        return referencedModel instanceof ReflectedResourceClass;
    }

    public boolean isValue() {
        return referencedModel instanceof ReflectedValueClass;
    }

    public boolean isEvent() {
        return referencedModel instanceof ReflectedEventClass;
    }

    @Override
    public boolean isCollection() {
        return multi;
    }

    @Override
    public String toString() {
        return String.format("%s reference of type %s, named '%s'; reference is %s a "
                + "collection.",
                (isResource()) ? "link" : "value",
                referencedModel.toString(),
                name,
                isCollection());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReflectedTypeRef<?> other = (ReflectedTypeRef<?>) obj;
        if (this.multi != other.multi) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.referencedModel, other.referencedModel);
    }

}
