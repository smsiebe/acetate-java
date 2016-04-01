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
import java.lang.reflect.Type;
import org.geoint.acetate.NotDomainResourceException;
import org.geoint.acetate.java.InvalidDomainMethodException;
import org.geoint.acetate.java.model.Accessor;
import org.geoint.acetate.model.InvalidModelException;

/**
 * Models an accessor method, which provides a reference to another domain type.
 *
 * @author steve_siebert
 * @param <T> java class representation of the domain type defining this
 * accessor
 * @param <R> java class representation of the domain type returned by this
 * accessor
 */
public class ReflectedAccessorMethod<T, R> extends ReflectedTypeRef<ReflectedTypeClass<R>> {

    private final Class<T> domainClass;
    private final Method method;

    protected ReflectedAccessorMethod(String name, String description, boolean multi,
            Class<T> resourceClass, Method method,
            ReflectedTypeClass<R> ref) {
        super(name, description, ref, multi);
        this.domainClass = resourceClass;
        this.method = method;
    }

    public static <T> ReflectedAccessorMethod<T, ?> forMethod(
            String domainNamespace, String domainType, String domainVersion,
            Class<T> domainClass,
            Method accessorMethod)
            throws InvalidModelException {

        if (accessorMethod.getParameterCount() > 0) {
            throw new InvalidDomainMethodException(domainNamespace,
                    domainType, domainVersion, domainClass, accessorMethod,
                    "Domain accessor method must not require parameters.");
        }

        Accessor a = accessorMethod.getAnnotation(Accessor.class);

        if (a == null) {
            throw new InvalidDomainMethodException(domainNamespace,
                    domainType, domainVersion, domainClass, accessorMethod,
                    "Domain accessor method must be annotated with @Accessor.");
        }

        Type returnType = accessorMethod.getGenericReturnType();
        Class<?> returnClass;
        boolean isCollection = DomainReflectionUtil.isCollection(returnType);
        if (isCollection) {
            returnClass= DomainReflectionUtil.getCollectionClass(returnType);
        } else if ()
        //TODO support array and map here too
     

        try {
            ReflectedTypeClass<?> returnModel = DomainReflectionUtil.model(returnClass);

            return new ReflectedAccessorMethod(
                    a.name(),
                    a.description(),
                    isCollection,
                    domainClass,
                    accessorMethod,
                    returnModel);
        } catch (NotDomainResourceException ex) {
            throw new InvalidDomainMethodException(domainNamespace,
                    domainType, domainVersion, domainClass, accessorMethod,
                    "Domain accessor method must return a domain type.", ex);
        }
    }

    public Class<T> getResourceClass() {
        return domainClass;
    }

    public Method getMethod() {
        return method;
    }

}
