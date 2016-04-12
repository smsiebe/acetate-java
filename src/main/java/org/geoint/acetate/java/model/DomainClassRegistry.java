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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.geoint.acetate.DomainInstantiationException;
import org.geoint.acetate.TypeInstance;
import org.geoint.acetate.format.TypeFormat;
import org.geoint.acetate.format.TypeFormatter;
import org.geoint.acetate.format.TypeParser;
import org.geoint.acetate.format.UnsupportedFormatException;
import org.geoint.acetate.format.spi.FormatFactory;
import org.geoint.acetate.format.spi.FormatServiceLoaderProvider;
import org.geoint.acetate.java.bind.ObjectBinder;
import org.geoint.acetate.java.format.ObjectFormatter;
import org.geoint.acetate.java.format.ObjectParser;
import org.geoint.acetate.java.model.reflect.ClassTypeReflector;
import org.geoint.acetate.model.DomainBuilder;
import org.geoint.acetate.model.DomainType;
import org.geoint.acetate.model.DuplicateNamedTypeException;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.model.TypeDescriptor;
import org.geoint.acetate.model.resolve.DomainTypeResolver;
import org.geoint.acetate.model.resolve.HierarchicalTypeResolver;
import org.geoint.acetate.model.resolve.MapTypeResolver;
import org.geoint.acetate.util.BidirectionalMap;

/**
 * Class-aware domain model registry.
 *
 * @author steve_siebert
 */
public class DomainClassRegistry {

    private final BidirectionalMap<Class<?>, TypeDescriptor> classDescriptors;
    protected final MapTypeResolver<TypeDescriptor> localRegistry;  //write
    protected final HierarchicalTypeResolver<TypeDescriptor> typeResolver; //read
    private final Map<TypeDescriptor, ObjectBinder> typeBinders;
    private final Set<ObjectBinder> defaultBinders;
    private final FormatFactory formatFactory;
    private final Map<Class<?>, ObjectFormatter> objectFormatters;
    private final Map<Class<?>, ObjectParser> objectParsers;

    public DomainClassRegistry() {
        this.localRegistry = new MapTypeResolver<>(new ConcurrentHashMap<>());
        this.typeResolver = HierarchicalTypeResolver.newHierarchy(localRegistry);
        this.classDescriptors = BidirectionalMap.newMap(() -> new HashMap<>());
        typeBinders = new ConcurrentHashMap<>();
        defaultBinders = Collections.synchronizedSet(new HashSet<>());
        this.objectFormatters = new ConcurrentHashMap<>();
        this.objectParsers = new ConcurrentHashMap<>();

        this.formatFactory = FormatFactory.getDefaultFactory();
        this.formatFactory.addProvider(new FormatServiceLoaderProvider());
    }

    public DomainClassRegistry(BidirectionalMap<Class<?>, TypeDescriptor> classDescriptors,
            DomainTypeResolver<TypeDescriptor> typeResolver) {
        this(classDescriptors, typeResolver, FormatFactory.getDefaultFactory());
    }

    public DomainClassRegistry(BidirectionalMap<Class<?>, TypeDescriptor> classDescriptors,
            DomainTypeResolver<TypeDescriptor> typeResolver,
            FormatFactory formatFactory) {
        this.localRegistry = new MapTypeResolver<>(new ConcurrentHashMap<>());
        this.typeResolver = HierarchicalTypeResolver.newHierarchy(typeResolver)
                .addChild(localRegistry);
        this.classDescriptors = classDescriptors;
        typeBinders = new ConcurrentHashMap<>();
        defaultBinders = Collections.synchronizedSet(new HashSet<>());
        this.objectFormatters = new ConcurrentHashMap<>();
        this.objectParsers = new ConcurrentHashMap<>();

        this.formatFactory = formatFactory;
        this.formatFactory.addProvider(new FormatServiceLoaderProvider());
    }

    public void register(Class<?> domainClass) throws InvalidModelException {

        TypeDescriptor td = ClassTypeReflector.getTypeDescriptor(domainClass);

        synchronized (classDescriptors) {
            if (classDescriptors.containsKey(domainClass)) {
                //already registered
                return;
            }

            classDescriptors.put(domainClass, td);
        }

        //if there isn't a model for this class, try using reflection
        synchronized (localRegistry) {
            if (!typeResolver.resolveType(td).isPresent()) {
                localRegistry.getTypes()
                        .put(td, ClassTypeReflector.model(domainClass,
                                new DomainBuilder(td.getNamespace(),
                                        td.getVersion(), typeResolver))
                        );
            }
        }

        ClassTypeReflector.binder(domainClass, (b) -> typeBinders.putIfAbsent(td, b));
        ClassTypeReflector.formatters(domainClass, (f) -> objectFormatters.put(domainClass, f));
        ClassTypeReflector.parsers(domainClass, (p) -> objectParsers.put(domainClass, p));

    }

    public void register(Class<?> domainClass, DomainType model)
            throws DuplicateNamedTypeException, InvalidModelException {

        TypeDescriptor td = model.getTypeDescriptor();

        //add model to local registry if now resolvable
        synchronized (localRegistry) {
            if (!typeResolver.resolveType(td).isPresent()) {
                localRegistry.getTypes().put(td, model);
            }
        }

        //map class to model if not already done
        synchronized (classDescriptors) {
            if (classDescriptors.containsKey(domainClass)) {
                return;
            }
            classDescriptors.put(domainClass, td);
        }

        ClassTypeReflector.binder(domainClass, (b) -> typeBinders.putIfAbsent(td, b));
        ClassTypeReflector.formatters(domainClass, (f) -> objectFormatters.put(domainClass, f));
        ClassTypeReflector.parsers(domainClass, (p) -> objectParsers.put(domainClass, p));
    }

    public FormatFactory getFormatFactory() {
        return formatFactory;
    }

    public void register(TypeDescriptor td, ObjectBinder binder) {
        typeBinders.put(td, binder);
    }

    public void registerDefaultBinder(ObjectBinder binder) {
        defaultBinders.add(binder);
    }

    public Optional<Class<?>> findClass(TypeDescriptor td) {
        return classDescriptors.findKey(td);
    }

    public Optional<DomainType> findType(Class<?> domainClass) {
        return classDescriptors.findValue(domainClass)
                .map((td) -> typeResolver.resolveType(td).orElse(null));
    }

    public TypeParser findParser(TypeFormat format)
            throws UnsupportedFormatException {
        return formatFactory.getParser(format);
    }

    public TypeFormatter findFormatter(TypeFormat format)
            throws UnsupportedFormatException {
        return formatFactory.getFormatter(format);
    }

    /**
     * This method will only return a binder if it has been specifically defined
     * for a domain type and does not return any possible matching default
     * binders.
     *
     * @param td
     * @return
     */
    public Optional<ObjectBinder> findBinder(TypeDescriptor td) {
        return Optional.ofNullable(typeBinders.get(td));
    }

    /**
     * Attempts to bind the type instance to a java object using the best binder
     * known to the registry.
     *
     * @param instance domain instance
     * @return java object representation of the domain instance
     * @throws DomainInstantiationException if the binder cannot be used for
     * type type instance/class
     */
    public Object asObject(TypeInstance instance) throws DomainInstantiationException {
        Optional<ObjectBinder> binder = findBinder(instance.getTypeDescriptor());
        if (binder.isPresent()) {
            return binder.get().asObject(instance);
        }

        //TODO make this default binder process better than trial and error
        return defaultBinders.parallelStream()
                .map((b) -> {
                    try {
                        return b.asObject(instance);
                    } catch (Throwable ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DomainInstantiationException(
                        String.format("Unable to resolve ObjectBinder for "
                                + "type '%s'",
                                instance.getTypeDescriptor().toString())));
    }

    /**
     * Attempts to convert a java object to a TypeInstance using the best binder
     * known to the registry for the type.
     *
     * @param object source object
     * @return domain type instance
     * @throws DomainInstantiationException if the binder cannot be used for
     * type type instance/class
     * @throws InvalidModelException if the object is not a type representation
     */
    public TypeInstance asType(Object object)
            throws DomainInstantiationException, InvalidModelException {
        Optional<TypeDescriptor> desc = classDescriptors.findValue(object.getClass());

        if (!desc.isPresent()) {
            TypeDescriptor td = ClassTypeReflector.getTypeDescriptor(object.getClass());

            Optional<ObjectBinder> binder = findBinder(td);
            if (binder.isPresent()) {
                return binder.get().asType(object);
            }
        }

        //TODO make this default binder process better than trial and error
        return defaultBinders.parallelStream()
                .map((b) -> {
                    try {
                        return b.asType(object);
                    } catch (Throwable ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DomainInstantiationException(
                        String.format("Unable to resolve ObjectBinder for "
                                + "object class '%s'",
                                object.getClass().getName())));
    }
}
