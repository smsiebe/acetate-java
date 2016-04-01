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
import org.geoint.acetate.java.model.DomainResource;
import org.geoint.acetate.java.model.OperationMethodModel;
import org.geoint.acetate.java.model.ResourceClass;
import org.geoint.acetate.java.model.TypeClassRef;
import org.geoint.acetate.java.model.ValueClass;
import org.geoint.acetate.model.InvalidModelException;

/**
 * Resource model described by annotations and read through reflection.
 *
 * @author steve_siebert
 * @param <T> java class representing a domain resource
 */
public class ReflectedResourceClass<T> extends ReflectedTypeClass<T>
        implements ResourceClass<T> {

    private final Map<String, ReflectedOperationMethod<T, ?>> operations;
    private final Map<String, ReflectedAccessorMethod> attributes;
    private final Map<String, ReflectedAccessorMethod> links;

    private ReflectedResourceClass(Class<T> resourceClass, String namespace,
            String version, String resourceType, String desc) {
        super(resourceClass, namespace, version, resourceType, desc);
        this.operations = new HashMap<>();
        this.attributes = new HashMap<>();
        this.links = new HashMap<>();
        this.scanned = false;
    }

    public static <T> ReflectedResourceClass<T> forClass(Class<T> resourceClass)
            throws InvalidModelException {
        if (!resourceClass.isAnnotationPresent(DomainResource.class)) {
            throw new NotDomainResourceException(resourceClass);
        }

        DomainResource domain = resourceClass.getAnnotation(DomainResource.class);
        return new ReflectedResourceClass(resourceClass, domain.namespace(),
                domain.version(), domain.type(), domain.description());
    }

    @Override
    public Set<OperationMethodModel<T, ?>> getOperations()
            throws InvalidModelException {
        return scanThanGet(() -> new HashSet(operations.values()));
    }

    @Override
    public Optional<OperationMethodModel<T,?>> findOperation(String operationName)
            throws InvalidModelException {
        return findOrScan(() -> operations.get(operationName));
    }

    @Override
    public Set<TypeClassRef<ValueClass<?>>> getAttributes()
            throws InvalidModelException {
        return scanThanGet(() -> new HashSet(attributes.values()));
    }

    @Override
    public Optional<TypeClassRef<ValueClass<?>>> findAttribute(
            String attributeName) throws InvalidModelException {
        return findOrScan(() -> attributes.get(attributeName));
    }

    @Override
    public Set<TypeClassRef<ResourceClass<?>>> getLinks()
            throws InvalidModelException {
        return scanThanGet(() -> new HashSet(links.values()));
    }

    @Override
    public Optional<TypeClassRef<ResourceClass<?>>> findLink(
            String linkName) throws InvalidModelException {
        return findOrScan(() -> links.get(linkName));
    }

    @Override
    protected void addAccessor(ReflectedAccessorMethod accessorModel)
            throws InvalidDomainMethodException {

        if (accessorModel.isValue()) { // || accessorModel.getRef().isEvent()) {
            if (attributes.containsKey(accessorModel.getName())) {
                throw new InvalidDomainMethodException(this, this.getDomainClass(),
                        accessorModel.getMethod(), String.format("Duplicate accessors found for "
                                + "attribute '%s'. Attribute accessor names "
                                + "must be unique for each resource.",
                                accessorModel.getName()));
            }
            attributes.put(accessorModel.getName(), accessorModel);
        } else if (accessorModel.isResource()) {
            if (links.containsKey(accessorModel.getName())) {
                throw new InvalidDomainMethodException(this, this.getDomainClass(),
                        accessorModel.getMethod(), String.format("Duplicate accessors found for "
                                + "link: '%s'.  Link accessor names must be "
                                + "unique for each resource.", accessorModel.getName()));
            }
            links.put(accessorModel.getName(), accessorModel);
        } else {
            throw new InvalidDomainMethodException(this.getDomainNamespace(),
                    this.getDomainType(),
                    this.getDomainVersion(),
                    this.getDomainClass(), accessorModel.getMethod(),
                    String.format("Domain accessor method return either a "
                            + "resource (link) or value type; accessor method "
                            + "'%s' returned domain type '%s'.",
                            accessorModel.getMethod().toString(),
                            accessorModel.getReferencedType().getClass().getName()));
        }
    }

    @Override
    protected void addOperation(ReflectedOperationMethod<T, ?> methodModel)
            throws InvalidModelException {
        if (operations.containsKey(methodModel.getName())) {
            throw new InvalidDomainMethodException(this, this.getDomainClass(),
                    methodModel.getOperationMethod(),
                    String.format("Duplicate operation found: '%s'.  "
                            + "Resource operation names must be unique.",
                            methodModel.getName()));
        }

        operations.put(methodModel.getName(), methodModel);
    }

}
