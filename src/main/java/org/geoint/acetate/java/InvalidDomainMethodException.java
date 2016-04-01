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
package org.geoint.acetate.java;

import java.lang.reflect.Method;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.model.DomainType;

/**
 * Thrown if a method is not a valid domain operation or accessor.
 *
 * @author steve_siebert
 */
public class InvalidDomainMethodException extends InvalidModelException {

    public InvalidDomainMethodException(DomainType domainType,
            Class<?> resourceClass, Method operationMethod) {
        this(domainType.getNamespace(), domainType.getName(),
                domainType.getVersion(), resourceClass, operationMethod);
    }

    public InvalidDomainMethodException(DomainType domainType,
            Class<?> resourceClass, Method operationMethod, String message) {
        this(domainType.getNamespace(), domainType.getName(),
                domainType.getVersion(), resourceClass, operationMethod,
                message);
    }

    public InvalidDomainMethodException(DomainType domainType,
            Class<?> resourceClass, Method operationMethod, String message,
            Throwable cause) {
        this(domainType.getNamespace(), domainType.getName(),
                domainType.getVersion(), resourceClass, operationMethod,
                message, cause);
    }

    public InvalidDomainMethodException(DomainType domainType,
            Class<?> resourceClass, Method operationMethod, Throwable cause) {
        this(domainType.getNamespace(), domainType.getName(),
                domainType.getVersion(), resourceClass, operationMethod,
                cause);
    }

    public InvalidDomainMethodException(String namespace, String type,
            String version, Class<?> resourceClass, Method operationMethod) {
        super(message(namespace, type, version,
                resourceClass, operationMethod, null));
    }

    public InvalidDomainMethodException(String namespace, String type,
            String version, Class<?> resourceClass, Method operationMethod,
            String message) {
        super(message(namespace, type, version,
                resourceClass, operationMethod, message));
    }

    public InvalidDomainMethodException(String namespace, String type,
            String version, Class<?> resourceClass, Method operationMethod,
            String message, Throwable cause) {
        super(message(namespace, type, version,
                resourceClass, operationMethod, message), cause);
    }

    public InvalidDomainMethodException(String namespace, String type,
            String version, Class<?> resourceClass, Method operationMethod,
            Throwable cause) {
        super(message(namespace, type, version,
                resourceClass, operationMethod, null), cause);
    }

    private static String message(String namespace, String type,
            String version, Class<?> domainClass, Method operationMethod,
            String msg) {
        return String.format("Method '%s' is not a valid domain method or "
                + "accessor on domain type '%s.%s-%s'. %s",
                operationMethod.toString(),
                namespace, type, version, String.format(msg));
    }
}
