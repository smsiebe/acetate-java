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

/**
 * Thrown when the resource instance version is not valid for the current
 * action.
 *
 * @author steve_siebert
 */
public class InvalidResourceVersionException extends DomainStateException {

    public InvalidResourceVersionException(String domainNamespace,
            String domainType, String domainVersion, String instanceGuid,
            String currentInstanceVersion, String conflictingInstanceVersion) {
        super(message(domainNamespace, domainType, domainVersion, instanceGuid,
                currentInstanceVersion, conflictingInstanceVersion, null));
    }

    public InvalidResourceVersionException(String domainNamespace,
            String domainType, String domainVersion, String instanceGuid,
            String currentInstanceVersion, String conflictingInstanceVersion, String message) {
        super(message(domainNamespace, domainType, domainVersion, instanceGuid,
                currentInstanceVersion, conflictingInstanceVersion, message));
    }

    public InvalidResourceVersionException(String domainNamespace,
            String domainType, String domainVersion, String instanceGuid,
            String currentInstanceVersion, String conflictingInstanceVersion,
            String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidResourceVersionException(String domainNamespace,
            String domainType, String domainVersion, String instanceGuid,
            String currentInstanceVersion, String conflictingInstanceVersion,
            Throwable cause) {
        super(cause);
    }

    private static String message(String domainNamespace,
            String domainType, String domainVersion, String instanceGuid,
            String currentInstanceVersion, String conflictingInstanceVersion,
            String msg) {
        return String.format("Resource '%s.%s-%s' instance '%s-%s' is not valid; "
                + "expecting instance version '%s'. %s",
                domainNamespace, domainType, domainVersion, instanceGuid,
                currentInstanceVersion, conflictingInstanceVersion,
                String.valueOf(msg));
    }

}
