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
package org.geoint.acetate.java.model;

import java.util.Optional;
import org.geoint.acetate.java.model.reflect.ClassNotDomainTypeException;
import org.geoint.acetate.model.DomainType;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.model.TypeDescriptor;
import org.geoint.acetate.model.TypeResolver;

/**
 * Extends a standard TypeResolver by also providing type-to-class and
 * class-to-type lookup service.
 *
 * @author steve_siebert
 */
public interface ClassTypeResolver extends TypeResolver {

    /**
     * Returns the type model, if the class represents a domain type.
     *
     * @param typeClass class representing a domain type
     * @return domain type
     * @throws ClassNotDomainTypeException if resolver cannot resolve a domain
     * type model for the java class
     * @throws InvalidModelException if the resolver recognizes the class as
     * representing a domain type, but its resolution creates an invalid domain
     * model
     */
    DomainType resolve(Class<?> typeClass)
            throws InvalidModelException, ClassNotDomainTypeException;

    /**
     * Returns the java class known to the resolver used to represent the domain
     * type.
     *
     * @param typeDesc domain type descriptor
     * @return java class used to represent the domain type or null
     */
    Optional<Class<?>> resolve(TypeDescriptor typeDesc);
}
