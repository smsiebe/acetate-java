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
import org.geoint.acetate.serialization.TypeCodec;

/**
 * Identifies a java class as a domain data value.
 *
 * @author steve_siebert
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DomainValue {

    /**
     * DomainResource namespace.
     *
     * @return domain namespace
     */
    String namespace();

    /**
     * Resource version.
     *
     * @return version as string
     */
    String version();

    /**
     * DomainResource-unique version type.
     *
     * @return version type
     */
    String type();

    /**
     * Optional resource description.
     *
     * @return optional resource description.
     */
    String description() default "";

    /**
     * Optional list of default codecs to use if not overridden by the
     * application.
     *
     * @return default value codecs
     */
    Class<? extends TypeCodec>[] defaultCodecs() default {};
}