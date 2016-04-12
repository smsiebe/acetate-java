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
import org.geoint.acetate.ResourceInstance;
import org.geoint.acetate.model.ResourceType;

/**
 *
 * @author steve_siebert
 * @param <T> java class representation of a domain resource
 */
public class ResourceObject<T> implements ResourceInstance, TypeObject<T, ResourceType> {

    private final ResourceType model;
    private final T obj;
    private final String guid;
    private final String version;
    private final Optional<String> previousVersion;

    public ResourceObject(ResourceType model, T obj, String guid,
            String version, Optional<String> previousVersion) {
        this.model = model;
        this.obj = obj;
        this.guid = guid;
        this.version = version;
        this.previousVersion = previousVersion;
    }

    @Override
    public String getResourceGuid() {
        return guid;
    }

    @Override
    public String getResourceVersion() {
        return version;
    }

    @Override
    public Optional<String> getPreviousResourceVersion() {
        return previousVersion;
    }

    @Override
    public ResourceType getModel() {
        return model;
    }

    public T asObject() {
        return obj;
    }

    @Override
    public Collection getComposites() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional findComposite(String compositeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection getLinks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional findLink(String linkName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional findOperation(String operationName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.model);
        hash = 67 * hash + Objects.hashCode(this.obj);
        hash = 67 * hash + Objects.hashCode(this.guid);
        hash = 67 * hash + Objects.hashCode(this.version);
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
        final ResourceObject<?> other = (ResourceObject<?>) obj;
        if (!Objects.equals(this.guid, other.guid)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.model, other.model)) {
            return false;
        }
        if (!Objects.equals(this.obj, other.obj)) {
            return false;
        }
        return true;
    }

}
