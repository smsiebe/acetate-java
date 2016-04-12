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

/**
 * Mock resource with valid annotations.
 * 
 * @author steve_siebert
 */
@DomainResource(namespace = MockDomainConstants.MOCK_NAMESPACE,
        version = MockDomainConstants.MOCK_VERSION,
        type = MockValidResource.RESOURCE_TYPE)
public class MockValidResource {

    public static final String RESOURCE_TYPE = "mockAnnotated";

    /*
     * this method is not annotated so expectation is that it is ignored
    */
    public String noDomainMethod() {
        return "fail";
    }

    
    public static final String OPERTION_NO_PARAM_NAME = "operationNoParam";
//    /*
//     * resource operation without parameters
//    */
//    @Operation
//    public MockValidEvent operationNoParam() {
//        return new MockValidEvent();
//    }
//
//    public static final String OPERATION_OVERRIDE_NAME = "fancyOperationName";
//    /*
//     * resource operation whoes name is override through annotation
//    */
//    @Operation(name=OPERATION_OVERRIDE_NAME)
//    public MockValidEvent overriddenName() {
//        return new MockValidEvent();
//    }
    
    public static final String ACCESSOR_MAP = "map";
//    @Accessor(name = ACCESSOR_MAP)
//    public Map<String, Integer> map() {
//        return Collections.EMPTY_MAP;
//    }
//
    
    public static final String ACCESSOR_ARRAY = "array";
//    @Accessor(name = ACCESSOR_ARRAY)
//    public String[] array() {
//        return new String[0];
//    }
//
    public static final String ACCESSOR_COLLECTION = "collection";
//    @Accessor(name = ACCESSOR_COLLECTION)
//    public Collection<String> collection() {
//        return Collections.EMPTY_LIST;
//    }
}
