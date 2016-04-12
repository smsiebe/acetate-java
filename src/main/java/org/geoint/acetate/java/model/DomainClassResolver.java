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

import org.geoint.acetate.model.resolve.BidirectionalTypeResolver;

/**
 * Resolves java classes to domain types and domain types to java classes.
 *
 * @author steve_siebert
 */
public interface DomainClassResolver extends BidirectionalTypeResolver<Class<?>> {
//
//    /**
//     * Returns the type model, if the class represents a domain type.
//     *
//     * @param typeClass class representing a domain type
//     * @return domain type descriptor
//     */
//    Optional<TypeDescriptor> resolveDescriptor(Class<?> typeClass);
//
//    /**
//     * Returns the java class known to the resolver used to represent the domain
//     * type.
//     *
//     * @param typeDesc domain type descriptor
//     * @return java class used to represent the domain type or null
//     */
//    Optional<Class<?>> resolveClass(TypeDescriptor typeDesc);
//
//    public default Optional<Class<?>> resolveClass(String namespace,
//            String version, String typeName) {
//        return resolveClass(new TypeDescriptor(namespace, version, typeName));
//    }

}
