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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.geoint.acetate.java.model.Accessor;
import org.geoint.acetate.java.model.DomainResource;
import org.geoint.acetate.java.model.Operation;

/**
 *
 * @author steve_siebert
 */
@DomainResource(namespace = MockAnnotatedResource.MOCK_NAMESPACE,
        version = MockAnnotatedResource.MOCK_VERSION,
        type = MockAnnotatedResource.RESOURCE_TYPE)
public class MockAnnotatedResource {

    public static final String OPERATION_NAME = "mockOperation";
    public static final String MOCK_NAMESPACE = "mock";
    public static final String MOCK_VERSION = "1.0";
    public static final String RESOURCE_TYPE = "mockAnnotated";
    public static final String ACCESSOR_ARRAY = "array";
    public static final String ACCESSOR_MAP = "map";
    public static final String ACCESSOR_COLLECTION = "collection";

    public String noDomainMethod() {
        return "fail";
    }

    @Operation(name = OPERATION_NAME)
    public MockAnnotatedEvent operation() {
        return new MockAnnotatedEvent();
    }

    @Accessor(name = ACCESSOR_MAP)
    public Map<String, Integer> map() {
        return Collections.EMPTY_MAP;
    }

    @Accessor(name = ACCESSOR_ARRAY)
    public String[] array() {
        return new String[0];
    }

    @Accessor(name = ACCESSOR_COLLECTION)
    public Collection<String> collection() {
        return Collections.EMPTY_LIST;
    }
}
