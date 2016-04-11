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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import org.geoint.acetate.functional.ThrowingFunction;
import org.geoint.acetate.java.model.reflect.ClassNotDomainTypeException;
import org.geoint.acetate.java.model.reflect.ClassTypeReflector;
import org.geoint.acetate.model.DomainType;
import org.geoint.acetate.model.HierarchicalTypeResolver;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.model.MemoryTypeResolver;
import org.geoint.acetate.model.TypeDescriptor;
import org.geoint.acetate.model.TypeResolver;

/**
 * Resolves domain types, caching resolved types if the model is created in the
 * process.
 * <p>
 * Instances of this resolver is thread-safe.
 *
 * @author steve_siebert
 */
public class CachingClassTypeResolver implements ClassTypeResolver {

    private final Map<DomainType, Class> typeClasses;
    private final Map<Class, DomainType> classTypes;
    private final TypeResolver typeResolver; //must handle as not thread-safe
    //used for synchronized access for above
    private final ReentrantReadWriteLock typeLock = new ReentrantReadWriteLock();

    public CachingClassTypeResolver() {
        this(null);
    }

    public CachingClassTypeResolver(TypeResolver resolver) {

        //TODO replace these with a two-way indexed map
        this.typeClasses = new HashMap<>();
        this.classTypes = new HashMap<>();

        if (resolver != null) {
            if (resolver instanceof HierarchicalTypeResolver) {
                //adds a locally-backed resolver against the class map
                this.typeResolver = ((HierarchicalTypeResolver) resolver)
                        .addChild(new MemoryTypeResolver(classTypes.values()));
            } else {
                //adds a locally-backed resolver against the class map
                this.typeResolver = HierarchicalTypeResolver.newHierarchy(resolver)
                        .addChild(new MemoryTypeResolver(classTypes.values()));
            }
        } else {
            //uses the class map to resolve
            this.typeResolver = new MemoryTypeResolver(classTypes.values());
        }

    }

    @Override
    public DomainType resolve(Class<?> typeClass)
            throws InvalidModelException, ClassNotDomainTypeException {
        return resolve(typeClass, (c) -> ClassTypeReflector.model(c, this));
    }

    /**
     * Calls the provided factory method to attempt to create the type model if
     * a type model is not known for the provided class.
     *
     * @param typeClass java class representation of a domain type
     * @param factory model creational method
     * @return domain type, if resolved
     * @throws ClassNotDomainTypeException if resolver cannot resolve a domain
     * type model for the java class
     * @throws InvalidModelException if the resolver recognizes the class as
     * representing a domain type, but its resolution creates an invalid domain
     * model
     */
    public DomainType resolve(Class<?> typeClass,
            ThrowingFunction<Class<?>, DomainType, InvalidModelException> factory)
            throws InvalidModelException, ClassNotDomainTypeException {

        ReadLock lock = typeLock.readLock();
        DomainType type = null;
        try {
            lock.lock();
            type = classTypes.get(typeClass);
            if (type != null) {
                return type;
            }
        } finally {
            lock.unlock();
        }

        //attempt to resolve using factory
        type = factory.apply(typeClass); //this throws ClassNotDomainTypeException if not found by reflector

        if (type != null) {

            WriteLock writeLock = typeLock.writeLock();
            try {
                writeLock.lock(); //OPTIMIZE consider using read-lock, than upgrading if necessary\
                //we resolved it..but lets make sure no other thread beat us
                if (classTypes.containsKey(typeClass)) {
                    return classTypes.get(typeClass);
                }
                //first to resolve
                typeClasses.put(type, typeClass);
                classTypes.put(typeClass, type);

            } finally {
                writeLock.unlock();
            }
        }

        throw new ClassNotDomainTypeException(typeClass);
    }

    @Override
    public Optional<Class<?>> resolve(TypeDescriptor typeDesc) {
        ReadLock lock = typeLock.readLock();
        try {
            lock.lock();
            return typeResolver.resolve(typeDesc.getNamespace(),
                    typeDesc.getVersion(), typeDesc.getType())
                    .map(typeClasses::get);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<DomainType> resolve(String namespace,
            String version, String typeName) {
        ReadLock lock = typeLock.readLock();
        try {
            lock.lock();
            return typeResolver.resolve(namespace, version, typeName);
        } finally {
            lock.unlock();
        }
    }
}
