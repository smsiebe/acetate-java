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
package org.geoint.acetate.spi.java;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import org.geoint.acetate.java.model.TypeClass;

/**
 * Instances of this extension provide class-to-model and model-to-class
 * resolution.
 * <p>
 * Implementations wishing to be automatically discovered and included in
 * resolution should make itself available in accordance with the
 * {@link ServiceLoader} specification.
 *
 * @author steve_siebert
 */
public interface DomainClassResolver {

    /**
     * Resolve a domain type model for the provided class.
     *
     * @param <T> java class representation of domain type
     * @param domainClass java class representation of domain type
     * @return the domain models to which this class can be resolved
     */
    <T> Optional<TypeClass<T>> find(Class<T> domainClass);

    /**
     *
     * @param <T>
     * @param namespace
     * @param type
     * @param version
     * @return
     */
    <T> Set<TypeClass<T>> find(String namespace, String type, String version);
}
