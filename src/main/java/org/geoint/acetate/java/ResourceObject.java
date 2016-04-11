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
import org.geoint.acetate.ResourceInstance;

/**
 * Provides access to resource metadata, the model, and a consistent API to
 * introspect the domain resource representable by a java object.
 * <p>
 * A domain resource instance must be able to be uniquely identified by a single
 * GUID/version pair.
 *
 * @author steve_siebert
 * @param <T> java class representation of a domain resource
 */
public interface ResourceObject<T> extends TypeObject<T>, ResourceInstance {

    @Override
    public Optional<OperationMethod<T, ?>> findOperation(String operationName);

    @Override
    public Set<OperationMethod<T, ?>> getOperations();

    @Override
    public Optional<ResourceObjectRef<?>> findLink(String linkName);

    @Override
    public Set<ResourceObjectRef<?>> getLinks();

    @Override
    public Optional<ValueObjectRef<?>> findAttribute(String attributeName);

    @Override
    public Set<ValueObjectRef<?>> getComposites();

    @Override
    public Optional<String> getPreviousResourceVersion();

    @Override
    public String getResourceVersion();

    @Override
    public String getResourceGuid();

    @Override
    public ResourceClass<T> getModel();

}
