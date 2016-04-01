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
package org.geoint.acetate.java.repo;

import org.geoint.acetate.java.EventObject;

/**
 * ResourceUpdater implementations may be registered with a
 * {@link ResourceRepository} to update the repository when a domain event is
 * committed for an instance managed by the repository.
 *
 * @author steve_siebert
 * @param <T> java class representing a domain resource
 * @param <E> java class representing the domain event
 */
@FunctionalInterface
public interface ResourceUpdater<T, E> {

    /**
     * Apply the event to the resource object, returning a new object instance
     * if there are any changes to the domain, otherwise return null.
     * <p>
     * The current resource is guaranteed to be not only the current instance 
     * but also the target instance of the event.  This method will not be 
     * called if these two conditions are not met.
     * 
     * @param currentResource current resource state
     * @param event event to apply
     * @return new resource state or null if no changes to the resource are
     * needed
     */
    T update(T currentResource, EventObject<E> event);

}
