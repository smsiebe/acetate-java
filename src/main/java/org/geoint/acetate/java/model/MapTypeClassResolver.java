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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.geoint.acetate.model.DomainType;
import org.geoint.acetate.util.BidirectionalMap;

/**
 * Instances of this resolver is thread-safe.
 *
 * @author steve_siebert
 */
public class MapTypeClassResolver implements DomainClassResolver {

    private final BidirectionalMap<Class<?>, DomainType> typeClasses;

    private MapTypeClassResolver(
            BidirectionalMap<Class<?>, DomainType> typeClasses) {
        this.typeClasses = typeClasses;
    }

    /**
     * Create a new map-backed domain type class resolver.
     *
     * @param mapFactory backing map factory
     * @return resolver
     */
    public static MapTypeClassResolver newInstance(Supplier<Map> mapFactory) {
        return new MapTypeClassResolver(BidirectionalMap.newMap(mapFactory));
    }

    @Override
    public Optional<DomainType> resolveType(Class<?> typeClass) {
        return typeClasses.findValue(typeClass);
    }

    @Override
    public Optional<Class<?>> resolveType(DomainType type) {
        return typeClasses.findKey(type);
    }

}
