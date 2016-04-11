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

import org.geoint.acetate.model.ResourceType;

/**
 * Thrown if a requested resource instance is not known to the domain.
 *
 * @author steve_siebert
 */
public class UnknownResourceException extends DomainStateException {

    public UnknownResourceException(Class<?> resourceClass,
            String instanceGuid, String instanceVersion) {
        super(message(resourceClass, instanceGuid,
                instanceVersion));
    }

    public UnknownResourceException(Class<?> resourceClass,
            String instanceGuid, String instanceVersion,
            Throwable cause) {
        super(message(resourceClass, instanceGuid,
                instanceVersion), cause);
    }

    public static String message(Class<?> resourceClass, String instanceGuid, String instanceVersion) {
        return String.format("Unknown resource '%s-%s' of type '%s'",
                instanceGuid,
                (instanceVersion == null) ? "latest" : instanceVersion,
                resourceClass);
    }

}
