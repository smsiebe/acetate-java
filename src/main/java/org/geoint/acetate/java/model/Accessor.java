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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the method of a java resource class as one which provides
 * {@link Idemponent} and {@link Safe} access to values and resources that
 * compose this resource.
 * <p>
 * A method cannot be annotated with both {@code @Accessor} and
 * {@code @Operation}.
 * <p>
 * Methods annotated with this may have {@code Safe} and {@code @Idemponent}
 * also annotated, but this is assumed and is not necessary.
 *
 * @see Operation
 * @see Idemponent
 * @see Safe
 * @author steve_siebert
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Accessor {

    /**
     * Specifies the local name of the domain type.
     *
     * @return local domain type name
     */
    String name();

    /**
     * Optional accessor description.
     *
     * @return accessor description
     */
    String description() default "";
}
