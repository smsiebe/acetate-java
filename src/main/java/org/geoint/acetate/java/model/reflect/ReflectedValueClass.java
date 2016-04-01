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

import java.util.HashSet;
import java.util.Set;
import org.geoint.acetate.NotDomainResourceException;
import org.geoint.acetate.java.InvalidDomainMethodException;
import org.geoint.acetate.java.model.DomainValue;
import org.geoint.acetate.java.model.ValueClass;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.serialization.TypeCodec;

/**
 *
 * @author steve_siebert
 * @param <T> java class representation of a domain value
 */
public class ReflectedValueClass<T> extends ReflectedTypeClass<T>
        implements ValueClass<T> {

    private final Set<TypeCodec> codecs;

    private ReflectedValueClass(Class<T> domainClass, String namespace,
            String version, String resourceType, String desc) {
        super(domainClass, namespace, version, resourceType, desc);
        codecs = new HashSet<>();
    }

    public static <T> ReflectedValueClass<T> forClass(Class<T> valueClass)
            throws InvalidModelException {

        if (!valueClass.isAnnotationPresent(DomainValue.class)) {
            throw new NotDomainResourceException(valueClass, String.format("Unable to "
                    + "create an value model from class '%s', no "
                    + "@DomainValue annotation is present.",
                    valueClass.getName()));
        }

        DomainValue a = valueClass.getAnnotation(DomainValue.class);

        return new ReflectedValueClass(valueClass, a.namespace(),
                a.type(), a.version(), a.description());
    }

    @Override
    protected void addAccessor(ReflectedAccessorMethod<T, ?> accessor)
            throws InvalidDomainMethodException {
        throw new InvalidDomainMethodException(this, this.getDomainClass(),
                accessor.getMethod(), "Domain values may not define accessors.");
    }

    @Override
    protected void addOperation(ReflectedOperationMethod<T, ?> opMethod)
            throws InvalidDomainMethodException {
        throw new InvalidDomainMethodException(this, this.getDomainClass(),
                opMethod.getOperationMethod(), "Domain values may not define "
                + "operations.");
    }

    @Override
    public Set<TypeCodec> getCodecs() {
        return codecs;
    }
}
