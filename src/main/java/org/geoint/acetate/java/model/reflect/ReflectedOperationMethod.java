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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.geoint.acetate.java.InvalidDomainMethodException;
import org.geoint.acetate.java.model.EventClass;
import org.geoint.acetate.java.model.Idempotent;
import org.geoint.acetate.java.model.Operation;
import org.geoint.acetate.java.model.OperationMethodModel;
import org.geoint.acetate.java.model.Param;
import org.geoint.acetate.java.model.Safe;
import org.geoint.acetate.java.model.TypeClass;
import org.geoint.acetate.java.model.TypeClassRef;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.model.NamedRef;
import org.geoint.acetate.model.NamedTypeRef;

/**
 *
 * @author steve_siebert
 * @param <R> java class representing the domain resource
 * @param <E> java class representing the domain event
 */
public class ReflectedOperationMethod<R, E> implements OperationMethodModel<R, E> {

    private final Class<R> resourceClass;
    private final String resourceNamespace;
    private final String resourceType;
    private final String resourceVersion;
    private final Method opMethod;
    private final String opName;
    private final Optional<String> description;
    private final boolean idempotent;
    private final boolean safe;
    private final List<NamedRef> parameters;
    private final TypeClassRef<EventClass<E>> result;

    private ReflectedOperationMethod(String resourceNamespace,
            String resourceType, String resourceVersion,
            Class<R> resourceClass, Method opMethod,
            String opName, String description, boolean idempotent,
            boolean safe, List<NamedRef> parameters,
            TypeClassRef<EventClass<E>> result) {
        this.resourceNamespace = resourceNamespace;
        this.resourceType = resourceType;
        this.resourceVersion = resourceVersion;
        this.resourceClass = resourceClass;
        this.opMethod = opMethod;
        this.opName = opName;
        this.description = Optional.ofNullable(
                (description == null || description.isEmpty())
                        ? null
                        : description);
        this.idempotent = idempotent;
        this.safe = safe;
        this.parameters = parameters;
        this.result = result;
    }

    public static <R> ReflectedOperationMethod<R, ?> forOpMethod(
            String resourceNamespace, String resourceType,
            String resourceVersion, Class<R> resourceClass, Method opMethod)
            throws InvalidModelException {

        Operation op = opMethod.getAnnotation(Operation.class);

        if (op == null) {
            throw new InvalidDomainMethodException(resourceNamespace,
                    resourceType, resourceVersion, resourceClass, opMethod,
                    "Domain operation method must be annotated with @Operation.");
        }

        final String opName = op.name();
        final String opDesc = op.description();

        final NamedRef returnNamedRef
                = ReflectedTypeRef.returnReference(opName, opDesc, opMethod);
        if (!TypeClassRef.class.isAssignableFrom(returnNamedRef.getClass())) {
            throw new InvalidDomainMethodException(resourceNamespace,
                    resourceType, resourceVersion, resourceClass, opMethod,
                    "Domain operation method must return a domain event type.");
        }
        final TypeClassRef<?> returnTypeRef = (TypeClassRef) returnNamedRef;
        final TypeClass<?> returnType = returnTypeRef.getReferencedType();

        if (!(returnType instanceof EventClass)) {
            throw new InvalidDomainMethodException(resourceNamespace,
                    resourceType, resourceVersion, resourceClass, opMethod,
                    "Domain operation method must return a domain event type.");
        }

        final EventClass<?> eventType = (EventClass) returnType;

        List<NamedRef> params = new ArrayList<>();
        for (Parameter p : opMethod.getParameters()) {

            Param ann = p.getAnnotation(Param.class);
            final String paramName = (ann != null && !ann.name().isEmpty())
                    ? ann.name()
                    : p.getName();
            final String paramDesc = (ann != null)
                    ? ann.description()
                    : "";
            NamedRef pModelRef
                    = ReflectedTypeRef.paramReference(paramName, paramDesc, p);
            params.add(pModelRef);
        }

        return new ReflectedOperationMethod(
                resourceNamespace, resourceType, resourceVersion,
                resourceClass, opMethod,
                opName, opDesc,
                opMethod.isAnnotationPresent(Idempotent.class),
                opMethod.isAnnotationPresent(Safe.class),
                params,
                returnTypeRef);
    }

    @Override
    public String getResourceNamespace() {
        return resourceNamespace;
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public String getResourceVersion() {
        return resourceVersion;
    }

    @Override
    public TypeClassRef<EventClass<E>> getSuccessEventType() {
        return result;
    }

    @Override
    public String getName() {
        return opName;
    }

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public boolean isIdempotent() {
        return idempotent;
    }

    @Override
    public boolean isSafe() {
        return safe;
    }

    @Override
    public List<? extends NamedRef> getParameters() {
        return parameters;
    }

    @Override
    public Class<R> getResourceClass() {
        return resourceClass;
    }

    @Override
    public Method getOperationMethod() {
        return opMethod;
    }

    @Override
    public String toString() {
        return String.format("Operation '%s' declared by resource class '%s'",
                opName, resourceClass.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.resourceClass);
        hash = 83 * hash + Objects.hashCode(this.opMethod);
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
        final ReflectedOperationMethod<?, ?> other = (ReflectedOperationMethod<?, ?>) obj;
        if (!Objects.equals(this.resourceClass, other.resourceClass)) {
            return false;
        }
        if (!Objects.equals(this.opMethod, other.opMethod)) {
            return false;
        }
        return true;
    }

}
