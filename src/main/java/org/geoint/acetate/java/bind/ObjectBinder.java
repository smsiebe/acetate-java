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
package org.geoint.acetate.java.bind;

import org.geoint.acetate.TypeInstance;
import org.geoint.acetate.model.TypeDescriptor;

/**
 * Binds a resource to a java object.
 * <p>
 * Implementations may be generic, such as a binder based on POJO data modeling,
 * or may be resource (class)-specific to support unique binding requirements.
 *
 * @author steve_siebert
 * @param <T> java class representation of a resource
 */
public interface ObjectBinder<T> {

    /**
     * Check if a binder can convert between the domain type and java class.
     *
     * @param type domain type
     * @param clazz java class
     * @return true if the binder can support this pair
     */
    boolean supports(TypeDescriptor type, Class<? super T> clazz);

    /**
     * Returns a java object representation of a domain type instance.
     * <p>
     * Note this method does not specify if the returned object must be newly
     * constructed or may be returned from cache. This is an
     * implementation-specific detail.
     *
     * @param instance domain instance
     * @return java object representation of the domain instance
     */
    T asObject(TypeInstance instance);

    /**
     * Returns an TypeInstance implementation that provides access to domain
     * defined data and behavior.
     *
     * @param object source object
     * @return domain type instance
     */
    TypeInstance asType(T object);

}
