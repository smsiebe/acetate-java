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

import java.util.Optional;
import org.geoint.acetate.java.model.ResourceClass;
import org.geoint.acetate.java.ResourceObject;

/**
 * Version-controlled storage of domain resource objects.
 *
 * @author steve_siebert
 * @param <T> java class representation of a domain resource
 */
public interface ResourceRepository<T> {

    /**
     * Model of the resource supported by this repository.
     *
     * @return supported resource model
     */
    ResourceClass<T> getModel();

    /**
     * Return the latest version of the resource instance, or null if no version
     * of the resource is unknown to this repository.
     *
     * @param guid resource instance guid
     * @return latest resource instance or null
     */
    Optional<ResourceObject<T>> findLatest(String guid);

    /**
     * Return the requested resource version or null if the specific resource
     * version is not known to this repository.
     *
     * @param guid resource instance guid
     * @param version resource instance version
     * @return resource instance for the requested version or null
     */
    Optional<ResourceObject<T>> find(String guid, String version);

    /**
     * Return the latest version of this resource or throws an exception if the
     * resource is unknown to this repository.
     *
     * @param guid resource instance guid
     * @return latest resource instance
     * @throws UnknownResourceException if the requested resource instance is
     * not know to this repository
     */
    public default ResourceObject<T> getLatest(String guid)
            throws UnknownResourceException {
        return findLatest(guid)
                .orElseThrow(()
                        -> new UnknownResourceException(
                                getModel().getDomainNamespace(),
                                getModel().getDomainType(),
                                getModel().getDomainVersion(),
                                guid, null));
    }

    /**
     * Return the requested version of the resource or throws an exception.
     *
     * @param guid resource instance guid
     * @param version resource instance verison
     * @return requested resource
     * @throws UnknownResourceException if the specific resource is not known to
     * this repository
     * @throws InvalidResourceVersionException if the resource is known but the
     * repository does not know of the specific resource version
     */
    public default ResourceObject<T> get(String guid, String version)
            throws UnknownResourceException, InvalidResourceVersionException {
        return find(guid, version)
                .orElseThrow(() -> new UnknownResourceException(
                        getModel().getDomainNamespace(),
                        getModel().getDomainType(),
                        getModel().getDomainVersion(),
                        guid, version));
    }

    /**
     * Adds a resource update handler which will be called on any matching
     * events.
     *
     * @param updater resource updater
     */
    void onEvent(ResourceUpdater<T, ?> updater);
}
