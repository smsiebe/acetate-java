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
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.geoint.acetate.java.InvalidDomainMethodException;
import org.geoint.acetate.java.model.Accessor;
import org.geoint.acetate.java.model.Operation;
import org.geoint.acetate.java.model.TypeClass;
import org.geoint.acetate.model.InvalidModelException;

/**
 * Abstracts away common domain/reflection behavior, leaving implementations to
 * handle model-specific behavior.
 *
 * @author steve_siebert
 * @param <T> java class representation of the domain type
 */
public abstract class ReflectedTypeClass<T> implements TypeClass<T> {

    private final String namespace;
    private final String version;
    private final String resourceType;
    private final Optional<String> description;
    private final Class<T> domainClass;
    protected volatile boolean scanned;

    private static final Logger LOGGER
            = Logger.getLogger(ReflectedTypeClass.class.getName());

    protected ReflectedTypeClass(Class<T> domainClass, String namespace,
            String version, String resourceType, String desc) {
        this.namespace = namespace;
        this.version = version;
        this.resourceType = resourceType;
        this.domainClass = domainClass;
        this.description = Optional.ofNullable(
                (desc == null || desc.isEmpty())
                        ? null
                        : desc);
    }

    @Override
    public String getDomainNamespace() {
        return this.namespace;
    }

    @Override
    public String getDomainType() {
        return resourceType;
    }

    @Override
    public String getDomainVersion() {
        return version;
    }

    @Override
    public Class<T> getDomainClass() {
        return domainClass;
    }

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    /**
     * Do complete scan of class for associated domain content.
     *
     * @throws InvalidModelException thrown if there is a problem with modeling
     * of the class
     */
    public synchronized void scan() throws InvalidModelException {
        if (scanned) {
            return;
        }

        for (Method m : this.getDomainClass().getMethods()) {
            if (m.isAnnotationPresent(Operation.class)) {
                if (m.isAnnotationPresent(Accessor.class)) {
                    throw new InvalidDomainMethodException(this,
                            this.domainClass, m,
                            "Method may not be declared as both a domain "
                            + "operation and an accessor.");
                }

                ReflectedOperationMethod op
                        = ReflectedOperationMethod.forOpMethod(this.namespace,
                                this.resourceType, this.version, domainClass, m);

                LOGGER.finer(String.format("Method '%s' was determined to "
                        + "be operation '%s' on type '%s'",
                        m.toString(), op.getName(), this.toString()));

                addOperation(op);
            } else if (m.isAnnotationPresent(Accessor.class)) {

                ReflectedAccessorMethod accessor
                        = ReflectedAccessorMethod.forMethod(this.namespace,
                                this.resourceType, this.version, domainClass, m);

                LOGGER.finer(String.format("Method '%s' was determined to "
                        + "be an accessor for '%s' on type '%s'",
                        m.toString(), accessor.getName(), this.toString()));

                addAccessor(accessor);

            } else {
                LOGGER.finer(String.format("Method '%s' was not determined "
                        + "to be a domain artifact.", m.toString()));
            }
        }
        scanned = true;
    }

    @Override
    public String toString() {
        return String.format("%s.%s-%s", namespace,
                resourceType, version);
    }

    protected <R> R scanThanGet(Supplier<R> getter)
            throws InvalidModelException {
        scan();
        return getter.get();
    }

    protected <R> Optional<R> findOrScan(Supplier<R> getter)
            throws InvalidModelException {
        R result = getter.get();
        if (result == null) {
            scan();
            result = getter.get();
        }
        return Optional.ofNullable(result);
    }

    protected abstract void addAccessor(ReflectedAccessorMethod<T, ?> accessor)
            throws InvalidModelException;

    protected abstract void addOperation(ReflectedOperationMethod<T, ?> opMethod)
            throws InvalidModelException;
}
