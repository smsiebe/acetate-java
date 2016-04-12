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
package org.geoint.acetate.java.bind;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.geoint.acetate.EventInstance;
import org.geoint.acetate.InstanceRef;
import org.geoint.acetate.model.EventType;

/**
 *
 * @author steve_siebert
 * @param <T>
 */
public class EventObject<T> implements EventInstance, TypeObject<T, EventType>{

    private final EventType model;
    private final T obj;

    public EventObject(EventType model, T obj) {
        this.model = model;
        this.obj = obj;
    }

    @Override
    public Collection<InstanceRef> getComposites() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<InstanceRef> findComposite(String compositeName) {

        throw new UnsupportedOperationException();
    }

    @Override
    public EventType getModel() {
        return model;
    }

    public T asObject() {
        return obj;
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.model);
        hash = 53 * hash + Objects.hashCode(this.obj);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventObject<?> other = (EventObject<?>) obj;
        if (!Objects.equals(this.model, other.model)) {
            return false;
        }
        if (!Objects.equals(this.obj, other.obj)) {
            return false;
        }
        return true;
    }

}
