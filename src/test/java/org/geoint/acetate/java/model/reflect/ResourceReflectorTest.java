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

//import java.lang.reflect.Method;
//import java.util.Optional;
//import org.geoint.acetate.NotDomainResourceException;
//import org.geoint.acetate.java.MockAnnotatedResource;
//import org.geoint.acetate.java.model.OperationMethodModel;
//import org.geoint.acetate.java.model.ResourceClass;
//import org.geoint.acetate.java.model.TypeClass;
//import org.geoint.acetate.model.OperationModel;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;

/**
 * Test resource model generation through reflection.
 *
 * @author steve_siebert
 */
public class ResourceReflectorTest {

//    /**
//     * Test the a java class representing a domain resource is processed
//     * correctly with reflection.
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testResourceClassReflection() throws Exception {
//        TypeClass<MockAnnotatedResource> typeModel
//                = ClassTypeReflector.model(MockAnnotatedResource.class);
//
//        assertTrue(ResourceClass.class.isAssignableFrom(typeModel.getClass()));
//        ResourceClass<MockAnnotatedResource> resource
//                = (ResourceClass<MockAnnotatedResource>) typeModel;
//
//        assertEquals(MockAnnotatedResource.MOCK_NAMESPACE, resource.getDomainNamespace());
//        assertEquals(MockAnnotatedResource.RESOURCE_TYPE, resource.getDomainType());
//        assertEquals(MockAnnotatedResource.MOCK_VERSION, resource.getDomainVersion());
//        assertEquals(MockAnnotatedResource.class, resource.getDomainClass());
//
//        assertEquals(1, resource.getOperations().size());
//
//        Optional<OperationMethodModel<MockAnnotatedResource, ?>> op
//                = resource.findOperation(MockAnnotatedResource.OPERATION_NAME);
//        assertTrue(op.isPresent());
//        OperationMethodModel<MockAnnotatedResource, ?> o = op.get();
//        assertEquals(MockAnnotatedResource.MOCK_NAMESPACE, o.getResourceNamespace());
//        assertEquals(MockAnnotatedResource.RESOURCE_TYPE, o.getResourceType());
//        assertEquals(MockAnnotatedResource.MOCK_VERSION, o.getResourceVersion());
//        assertEquals(MockAnnotatedResource.OPERATION_NAME, o.getName());
//        assertEquals(MockAnnotatedResource.class, o.getResourceClass());
//
//    }
//
//    @Test(expected = NotDomainResourceException.class)
//    public void testNoResourceClassException() throws Exception {
//        ClassTypeReflector.model(String.class);
//    }
//
//    @Test
//    public void testResourceOperationMethodReflection() throws Exception {
//        Method m = MockAnnotatedResource.class.getMethod("operation");
//        OperationModel o = ClassTypeReflector.forMethod(m);
//
//        assertEquals(MockAnnotatedResource.MOCK_NAMESPACE, o.getNamespace());
//        assertEquals(MockAnnotatedResource.RESOURCE_TYPE, o.getResourceType());
//        assertEquals(MockAnnotatedResource.MOCK_VERSION, o.getResourceVersion());
//        assertEquals(MockAnnotatedResource.OPERATION_NAME, o.getName());
//        assertEquals(MockAnnotatedResource.class, o.getResourceClass());
//    }
//
//    @Test(expected = NotDomainResourceException.class)
//    public void testNoResourceMethodException() throws Exception {
//        Method m = MockAnnotatedResource.class.getMethod("notOperation");
//        ClassTypeReflector.forMethod(m);
//    }
}
