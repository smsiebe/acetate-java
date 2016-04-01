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

import org.geoint.acetate.DomainException;

/**
 * Implementations of this exception are thrown if the framework was unable to
 * resolve a domain (instance) state problem.
 *
 * @author steve_siebert
 */
public abstract class DomainStateException extends DomainException {

    public DomainStateException(String message) {
        super(message);
    }

    public DomainStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainStateException(Throwable cause) {
        super(cause);
    }

}
