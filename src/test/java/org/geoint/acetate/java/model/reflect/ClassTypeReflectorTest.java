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

import org.geoint.acetate.java.model.DomainClassRegistry;
import org.geoint.acetate.java.model.MockDomainConstants;
import org.geoint.acetate.java.model.MockValidValue;
import org.geoint.acetate.model.DomainBuilder;
import org.geoint.acetate.model.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author steve_siebert
 */
public class ClassTypeReflectorTest {

    public ClassTypeReflectorTest() {
    }

    /**
     * Test reflection modeling is correct against a correctly defined domain
     * value type.
     */
    @Test
    public void testModelValidValue() throws Exception {

        ValueType type = ClassTypeReflector.modelValue(MockValidValue.class,
                newMockDomainBuilder());

        assertEquals(MockDomainConstants.MOCK_NAMESPACE, type.getNamespace());
        assertEquals(MockDomainConstants.MOCK_VERSION, type.getVersion());
        assertEquals(MockValidValue.MOCK_VALUE_TYPE, type.getName());

    }

    @Test
    public void testModelValidEvent() throws Exception {
        DomainClassRegistry reg = new DomainClassRegistry();
//        reg.register(MockValidEvent.class);
//        reg.register(MockValidValue.class);
//        
//        
//        EventType type = ClassTypeReflector.modelEvent(MockValidEvent.class,
//                newMockDomainBuilder());
//
//        assertEquals(MockDomainConstants.MOCK_NAMESPACE, type.getNamespace());
//        assertEquals(MockDomainConstants.MOCK_VERSION, type.getVersion());
//        assertEquals(MockValidEvent.EVENT_TYPE_NAME, type.getName());
//
//        Optional<NamedRef> compositeRef = type.findComposite(MockValidEvent.EVENT_COMPOSITE_NAME);
//        assertTrue(compositeRef.isPresent());
//        final NamedRef ref = compositeRef.get();
//        assertTrue(ref instanceof NamedTypeRef);
//        final NamedTypeRef vRef = (NamedTypeRef) ref;
//        assertFalse(vRef.isCollection());
//        final ValueType vType = (ValueType) vRef.getReferencedType();
//        assertEquals(MockDomainConstants.MOCK_NAMESPACE, vType.getNamespace());
//        assertEquals(MockDomainConstants.MOCK_VERSION, vType.getVersion());
//        assertEquals(MockValidValue.MOCK_VALUE_TYPE, vType.getName());
    }

    private DomainBuilder newMockDomainBuilder() {
        return new DomainBuilder(MockDomainConstants.MOCK_NAMESPACE, MockDomainConstants.MOCK_VERSION);
    }
}
