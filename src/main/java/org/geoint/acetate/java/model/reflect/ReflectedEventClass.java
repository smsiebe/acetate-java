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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.geoint.acetate.NotDomainResourceException;
import org.geoint.acetate.java.InvalidDomainMethodException;
import org.geoint.acetate.java.model.DomainEvent;
import org.geoint.acetate.java.model.EventClass;
import org.geoint.acetate.model.InvalidModelException;

/**
 *
 * @author steve_siebert
 * @param <E> java class representing a domain event
 */
public class ReflectedEventClass<E> extends ReflectedTypeClass<E>
        implements EventClass<E> {

    private final Map<String, ReflectedAccessorMethod> attributes;

    public ReflectedEventClass(Class<E> domainClass, String namespace,
            String version, String resourceType, String desc) {
        super(domainClass, namespace, version, resourceType, desc);
        this.attributes = new HashMap<>();
    }

    public static <E> ReflectedEventClass<E> forClass(Class<E> eventClass)
            throws InvalidModelException {

        if (!eventClass.isAnnotationPresent(DomainEvent.class)) {
            throw new NotDomainResourceException(eventClass, String.format("Unable to "
                    + "create an DomainEventModel from class '%s', no "
                    + "@DomainEvent annotation is present.",
                    eventClass.getName()));
        }

        DomainEvent a = eventClass.getAnnotation(DomainEvent.class);

        return new ReflectedEventClass(eventClass, a.namespace(),
                a.type(), a.version(), a.description());
    }

    @Override
    public Set<ReflectedTypeRef<ReflectedValueClass>> getAttributes()
            throws InvalidModelException {
        return scanThanGet(() -> new HashSet(attributes.values()));
    }

    @Override
    public Optional<ReflectedTypeRef<ReflectedValueClass>> findAttribute(String name)
            throws InvalidModelException {
        return findOrScan(() -> attributes.get(name));
    }

    @Override
    protected void addAccessor(ReflectedAccessorMethod<E, ?> accessorModel)
            throws InvalidModelException {
        String name = accessorModel.getName();

        if (attributes.containsKey(name)) {
            throw new InvalidDomainMethodException(this, this.getDomainClass(),
                    accessorModel.getMethod(), String.format("Duplicate accessors found: '%s'.  Accessor "
                            + "names must be unique for each domain type.",
                            name));
        }

        if (!accessorModel.isValue()) {
            throw new InvalidDomainMethodException(this, this.getDomainClass(),
                    accessorModel.getMethod(), String.format("Events may only "
                            + "reference domain values, links to domain "
                            + "resources are permitted.",
                            name));
        }
        attributes.put(name, accessorModel);
    }

    @Override
    protected void addOperation(ReflectedOperationMethod<E, ?> methodModel)
            throws InvalidDomainMethodException {
        throw new InvalidDomainMethodException(this, this.getDomainClass(),
                methodModel.getOperationMethod(), 
                "Domain events may not define operations.");
    }
}
