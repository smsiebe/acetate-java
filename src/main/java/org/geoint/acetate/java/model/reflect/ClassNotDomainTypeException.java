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

import org.geoint.acetate.model.InvalidModelException;

/**
 * Thrown if a java class is attempted to be used as a domain type, but the
 * domain relationship cannot be determined.
 *
 * @author steve_siebert
 */
public class ClassNotDomainTypeException extends InvalidModelException {

    public ClassNotDomainTypeException(Class<?> clazz) {
        super(message(clazz, null));
    }

    public ClassNotDomainTypeException(Class<?> clazz, String message) {
        super(message(clazz, message));

    }

    public ClassNotDomainTypeException(Class<?> clazz, String message, Throwable cause) {
        super(message(clazz, message), cause);

    }

    public ClassNotDomainTypeException(Class<?> clazz, Throwable cause) {
        super(message(clazz, null), cause);

    }

    private static String message(Class<?> clazz, String msg) {
        return String.format("Class '%s' is not known as a domain type "
                + "reference. %s", clazz.getName(), msg);
    }

}
