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

import java.util.Optional;
import java.util.Set;
import org.geoint.acetate.EventInstance;

/**
 * An EventInstance which is representable by a java object.
 * <p>
 * The object representation of the domain event contains the domain-specific
 * event characteristics, which this "wrapper" provide access to the metadata
 * for the event.
 *
 * @author steve_siebert
 * @param <T>
 */
public interface EventObject<T> extends TypeObject<T>, EventInstance {

    /**
     * References to type instances that compose the event.
     *
     * @return event instance composite references
     */
    @Override
    Set<ObjectRef> getComposites();

    /**
     * Retrieve event attribute by name.
     *
     * @param compositeRefName composite reference name
     * @return composite reference or null
     */
    @Override
    Optional<ObjectRef> findComposite(String compositeRefName);
}
