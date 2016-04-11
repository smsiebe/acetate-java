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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import org.geoint.acetate.functional.ThrowingConsumer;
import org.geoint.acetate.java.model.Accessor;
import org.geoint.acetate.java.model.ClassTypeResolver;
import org.geoint.acetate.java.model.DomainEvent;
import org.geoint.acetate.java.model.DomainResource;
import org.geoint.acetate.java.model.DomainValue;
import org.geoint.acetate.java.model.Operation;
import org.geoint.acetate.java.model.Param;
import org.geoint.acetate.model.DomainBuilder;
import org.geoint.acetate.model.DomainBuilder.ComposedTypeBuilder;
import org.geoint.acetate.model.DomainBuilder.EventBuilder;
import org.geoint.acetate.model.DomainBuilder.OperationBuilder;
import org.geoint.acetate.model.DomainBuilder.ResourceBuilder;
import org.geoint.acetate.model.DomainBuilder.ValueBuilder;
import org.geoint.acetate.model.DomainModel;
import org.geoint.acetate.model.DomainType;
import org.geoint.acetate.model.EventType;
import org.geoint.acetate.model.InvalidModelException;
import org.geoint.acetate.model.ResourceType;
import org.geoint.acetate.model.TypeDescriptor;
import org.geoint.acetate.model.ValueType;

/**
 * Uses reflection to introspect a java class definition to describe the domain
 * model it represents.
 *
 * @author steve_siebert
 */
public class ClassTypeReflector {

    /**
     * Returns a domain model this java class represents, if the class uses
     * annotations to describe the domain model.
     *
     * @param clazz java class representing a domain type
     * @return model of the domain type or null if this class does not describe
     * a model
     * @throws InvalidModelException if the class does represent a domain model
     * type, but the definition creates an invalid model
     * @throws ClassNotDomainTypeException if the class does not represent a
     * domain model type
     */
    public static DomainType model(Class<?> clazz, ClassTypeResolver resolver)
            throws InvalidModelException, ClassNotDomainTypeException {
        if (clazz.isAnnotationPresent(DomainResource.class)) {
            return modelResource(clazz, resolver);
        }
        if (clazz.isAnnotationPresent(DomainEvent.class)) {
            return modelEvent(clazz, resolver);
        }
        if (clazz.isAnnotationPresent(DomainValue.class)) {
            return modelValue(clazz, resolver);
        }
        throw new ClassNotDomainTypeException(clazz, "Domain model annotations "
                + "not found on class definition.");
    }

    /**
     * Returns the resource model defined by this class.
     *
     * @param clazz class to model
     * @return resource model if the class represents a domain resource
     * @throws InvalidModelException thrown if the class definition creates an
     * invalid model
     * @throws ClassNotDomainTypeException if the class does not represent a
     * domain model type
     */
    protected static ResourceType modelResource(Class<?> clazz, ClassTypeResolver resolver)
            throws InvalidModelException, ClassNotDomainTypeException {

        DomainResource domain = clazz.getAnnotation(DomainResource.class);
        if (domain == null) {
            throw new ClassNotDomainTypeException(clazz, "Class does not "
                    + "represent a domain resource.");
        }

        DomainBuilder db
                = new DomainBuilder(domain.namespace(), domain.version(), resolver);
        ResourceBuilder rb = db.defineResource(domain.type())
                .withDescription(domain.description());

        processDomainMethods(clazz,
                (m) -> defineAccessor(m, rb),
                (m) -> defineOperation(m, rb));

        DomainModel model = db.build();
        return model.getResource(domain.type());
    }

    /**
     * Returns the event model defined by this class.
     *
     * @param clazz class to model
     * @return event model if the class represents a domain event
     * @throws InvalidModelException thrown if the class definition creates an
     * invalid model
     */
    protected static EventType modelEvent(Class<?> clazz, ClassTypeResolver resolver)
            throws InvalidModelException {

        DomainEvent event = clazz.getAnnotation(DomainEvent.class);
        if (event == null) {
            throw new ClassNotDomainTypeException(clazz, "Class does not "
                    + "represent a domain event.");
        }

        DomainBuilder db
                = new DomainBuilder(event.namespace(), event.version(), resolver);
        EventBuilder eb = db.defineEvent(event.type())
                .withDescription(event.description());
        processDomainMethods(clazz,
                (m) -> defineAccessor(m, eb),
                (m) -> {
                    throw new InvalidDomainMethodException(m, "Event types cannot "
                            + "defined operations.");
                });
        DomainModel model = db.build();
        return model.getEvent(event.type());
    }

    /**
     * Returns the value model defined by this class, or null if this class does
     * not define a value model event.
     *
     * @param clazz class to model
     * @return value model if the class represents a domain value, otherwise
     * null
     * @throws InvalidModelException thrown if the class definition creates an
     * invalid model
     */
    protected static ValueType modelValue(Class<?> clazz, ClassTypeResolver resolver)
            throws InvalidModelException {

        DomainValue value = clazz.getAnnotation(DomainValue.class);
        if (value == null) {
            throw new ClassNotDomainTypeException(clazz, "Class does not "
                    + "represent a domain value.");
        }
        DomainBuilder db
                = new DomainBuilder(value.namespace(), value.version(), resolver);
        ValueBuilder vb = db.defineValue(value.type())
                .withDescription(value.description());
        processDomainMethods(clazz,
                (r) -> {
                    throw new InvalidModelException("Values types cannot be "
                            + "composed from other types.");
                },
                (r) -> {
                    throw new InvalidModelException("Event types cannot "
                            + "defined operations.");
                });
        DomainModel model = db.build();
        return model.getValue(value.type());
    }

    protected static void processDomainMethods(Class<?> typeClass,
            ThrowingConsumer<Method, InvalidModelException> accessorConsumer,
            ThrowingConsumer<Method, InvalidModelException> operationConsumer)
            throws InvalidModelException {
        for (Method m : typeClass.getMethods()) {

            if (m.isAnnotationPresent(Operation.class)) {

                if (m.isAnnotationPresent(Accessor.class)) {
                    throw new InvalidDomainMethodException(m,
                            "Method may not be declared as both a domain "
                            + "operation and an accessor.");
                }
                operationConsumer.consume(m);
            } else if (m.isAnnotationPresent(Accessor.class)) {
                accessorConsumer.consume(m);
            } else {
                //method has not significance to the domain
            }
        }
    }

    protected static void defineAccessor(Method m, ComposedTypeBuilder b)
            throws InvalidModelException {

        if (m.getParameterCount() > 0) {
            throw new InvalidDomainMethodException(m,
                    "Domain accessor method must not require parameters.");
        }

        Class<?> returnClass = m.getReturnType();
        if (returnClass.equals(Void.class)) {
            throw new InvalidDomainMethodException(m, "Domain accessor method "
                    + "must return a valid domain type, not void.");
        }

        Accessor accessor = m.getAnnotation(Accessor.class);

        if (returnClass.isAnnotationPresent(DomainResource.class)) {
            if (!(b instanceof ResourceBuilder)) {
                throw new InvalidDomainMethodException(m, "Only resources may "
                        + "define resource links.");
            }
            DomainResource linkedResource = returnClass.getAnnotation(DomainResource.class);
            ResourceBuilder rb = (ResourceBuilder) b;
            rb.withLink(accessor.name(), linkedResource.namespace(),
                    linkedResource.version(),
                    linkedResource.type())
                    .withDescription(accessor.description())
                    .build();
        } else if (returnClass.isAnnotationPresent(DomainValue.class)) {
            DomainValue value = returnClass.getAnnotation(DomainValue.class);
            b.withCompositeType(accessor.name(),
                    value.namespace(),
                    value.version(),
                    value.type());
        } else if (returnClass.isAnnotationPresent(DomainEvent.class)) {
            //TODO add support for returning event types from accessor
            throw new InvalidDomainMethodException(m, "accessors returning "
                    + "event type is currently not supported.");
        } else {
            //TODO support collection/array return type
            //TODO support map return type
            //TODO support generic "type" (non-resource/non-event/non-value) return type
            //TODO verify recursive definitions work
            throw new InvalidDomainMethodException(m, String.format("Domain"
                    + "accessor method must return a valid domain type; '%s' "
                    + "could not be resolved by the domain.",
                    returnClass.getName()));
        }

//        Accessor accessor = m.getAnnotation(Accessor.class);
//
//        Type returnType = m.getGenericReturnType();
//
//        boolean isCollection = isCollection(returnType);
//        if (isCollection) {
//            returnClass = getCollectionClass(returnType);
//        } else if (isMap(returnType)) {
//
//        }
    }

    protected static void defineOperation(Method m, ResourceBuilder b)
            throws InvalidModelException {

        Operation op = m.getAnnotation(Operation.class);

        OperationBuilder ob = b.withOperation(op.name())
                .withDescription(op.description());

        final Class<?> returnClass = m.getReturnType();
        if (returnClass.isAnnotationPresent(DomainEvent.class)) {
            throw new InvalidDomainMethodException(m,
                    "Domain operation method must return a domain event type.");
        }
        //TODO support overriding the event ref name by annotating return 
        final TypeDescriptor returnDescriptor
                = getTypeDescriptor(returnClass);
//        final DomainEvent returnEventAnnotation
//                = returnClass.getAnnotation(DomainEvent.class);
        ob.createsEvent(returnDescriptor.getType(),
                returnDescriptor.getNamespace(),
                returnDescriptor.getVersion(),
                returnDescriptor.getType())
                .withDescription(op.description())
                .build();

        for (Parameter p : m.getParameters()) {
            Param ann = p.getAnnotation(Param.class);
            final TypeDescriptor paramDescriptor
                    = getTypeDescriptor(p.getType());
            final String paramName = (ann != null && !ann.name().isEmpty())
                    ? ann.name()
                    : p.getName();
            final String paramDesc = (ann != null)
                    ? ann.description()
                    : "";
            ob.withParameter(paramName,
                    paramDescriptor.getNamespace(),
                    paramDescriptor.getVersion(),
                    paramDescriptor.getType())
                    .withDescription(paramDesc)
                    //.isCollection(true)  //TODO add support for collection and map
                    .build();
        }
    }

    protected static TypeDescriptor getTypeDescriptor(Class<?> domainClass)
            throws InvalidModelException {
        if (domainClass.isAnnotationPresent(DomainResource.class)) {
            DomainResource ann = domainClass.getAnnotation(DomainResource.class);
            return new TypeDescriptor(ann.namespace(),
                    ann.version(),
                    ann.type());
        } else if (domainClass.isAnnotationPresent(DomainValue.class)) {
            DomainValue ann = domainClass.getAnnotation(DomainValue.class);
            return new TypeDescriptor(ann.namespace(),
                    ann.version(),
                    ann.type());
        } else if (domainClass.isAnnotationPresent(DomainEvent.class)) {
            DomainEvent ann = domainClass.getAnnotation(DomainEvent.class);
            return new TypeDescriptor(ann.namespace(),
                    ann.version(),
                    ann.type());
        } else {
            //TODO support collection/array return type
            //TODO support map return type
            //TODO support generic "type" (non-resource/non-event/non-value) return type
            //TODO verify recursive definitions work
            throw new ClassNotDomainTypeException(domainClass, String.format(
                    "Class does not describe its domain affiliation through "
                    + "a supported domain annotation."));
        }

    }

//    protected ResourceOperation modelOperationMethod(Method operationMethod)
//            throws InvalidModelException {
//
//        Operation op = opMethod.getAnnotation(Operation.class);
//
//        if (op == null) {
//            throw new InvalidDomainMethodException(resourceNamespace,
//                    resourceType, resourceVersion, resourceClass, opMethod,
//                    "Domain operation method must be annotated with @Operation.");
//        }
//
//        final String opName = op.name();
//        final String opDesc = op.description();
//
//        final NamedRef returnNamedRef
//                = ReflectedTypeRef.returnReference(opName, opDesc, opMethod);
//        if (!TypeClassRef.class.isAssignableFrom(returnNamedRef.getClass())) {
//            throw new InvalidDomainMethodException(resourceNamespace,
//                    resourceType, resourceVersion, resourceClass, opMethod,
//                    "Domain operation method must return a domain event type.");
//        }
//        final TypeClassRef<?> returnTypeRef = (TypeClassRef) returnNamedRef;
//        final TypeClass<?> returnType = returnTypeRef.getReferencedType();
//
//        if (!(returnType instanceof EventClass)) {
//            throw new InvalidDomainMethodException(resourceNamespace,
//                    resourceType, resourceVersion, resourceClass, opMethod,
//                    "Domain operation method must return a domain event type.");
//        }
//
//        final EventClass<?> eventType = (EventClass) returnType;
//
//        List<NamedRef> params = new ArrayList<>();
//        for (Parameter p : opMethod.getParameters()) {
//
//            Param ann = p.getAnnotation(Param.class);
//            final String paramName = (ann != null && !ann.name().isEmpty())
//                    ? ann.name()
//                    : p.getName();
//            final String paramDesc = (ann != null)
//                    ? ann.description()
//                    : "";
//            NamedRef pModelRef
//                    = ReflectedTypeRef.paramReference(paramName, paramDesc, p);
//            params.add(pModelRef);
//        }
//
//        return new ReflectedOperationMethod(
//                resourceNamespace, resourceType, resourceVersion,
//                resourceClass, opMethod,
//                opName, opDesc,
//                opMethod.isAnnotationPresent(Idempotent.class),
//                opMethod.isAnnotationPresent(Safe.class),
//                params,
//                returnTypeRef);
//    }
//    /**
//     * Returns a reference to the domain instance represented by the provided
//     * object.
//     *
//     * @param obj
//     * @return
//     * @throws InvalidModelException
//     * @throws ClassNotDomainTypeException
//     */
//    public InstanceRef model(Object obj)
//            throws InvalidModelException, ClassNotDomainTypeException {
//        if (isMulti(obj.getClass())) {
//            //still need to determine if is a map or not
//            if (isMap(obj.getClass())) {
//                return modelMapInstance(obj);
//            } else {
//                return modelInstanceCollection(obj);
//            }
//        }
//        return modelTypeInstance(obj);
//    }
//
//    protected TypeInstanceRef modelTypeInstance(Object obj)
//            throws InvalidModelException, ClassNotDomainTypeException {
//
//    }
//
//    protected MapInstanceRef modelMapInstance(Object obj)
//            throws InvalidModelException, ClassNotDomainTypeException {
//
//    }
//
//    protected TypeInstanceRef modelInstanceCollection(Object obj)
//            throws InvalidModelException, ClassNotDomainTypeException {
//
//    }
    /**
     * Indicates if the type is a array, a subclass of java.util.Collection, or
     * a subclass of java.util.Map.
     *
     * @param type type to check if the type may contain multiple object
     * instances
     * @return true if is a "collection" container
     */
    protected static boolean isMulti(Type type) {
        return isArray(type)
                || isCollection(type)
                || isMap(type);
    }

    /**
     * Check if the type is a subclass of java.util.Map.
     *
     * @param type type to check
     * @return true if the type is a subclass of java.util.Map
     */
    protected static boolean isMap(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return (type instanceof Class
                && Map.class.isAssignableFrom((Class) type));
    }

    /**
     * Check if the type is an array.
     *
     * @param type type to check
     * @return true if the type describes an array
     */
    protected static boolean isArray(Type type) {
        return type instanceof GenericArrayType
                || (type instanceof Class && ((Class) type).isArray());
    }

    /**
     * Check if the type is a subclass of java.util.Collection.
     *
     * @param type type to check
     * @return true if the type is a subclass of java.util.Collection
     */
    protected static boolean isCollection(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return type instanceof Class
                && Collection.class.isAssignableFrom((Class) type);

    }

    /**
     * Return the class defined by the java.util.collection generic parameter.
     *
     * @param type collection type
     * @return class defined by the generic parameter
     * @throws InvalidModelException thrown if not a collection or does not
     * define a generic type
     */
    protected static Class<?> getCollectionClass(Type type)
            throws InvalidModelException {
        if (type instanceof ParameterizedType) {
            try {
                return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new InvalidModelException(String.format("Collection '%s' does "
                        + "not appear to defined a generic type.",
                        type.toString()), ex);
            }
        }
        throw new InvalidModelException(String.format("Cannot determine domain "
                + "type of collection defined by type '%s'.", type.toString()));
    }

    /**
     * Return the class defined by the generic array.
     *
     * @param arrayType array type
     * @return type contained by the array
     * @throws InvalidModelException thrown if the type does not define a type
     */
    protected static Class<?> getArrayClass(Type arrayType)
            throws InvalidModelException {
        if (arrayType instanceof GenericArrayType) {
            return (Class) ((GenericArrayType) arrayType).getGenericComponentType();
        } else if (arrayType instanceof Class) {
            Class<?> clazz = (Class) arrayType;
            if (clazz.isArray()) {
                return clazz.getComponentType();
            }
        }
        throw new InvalidModelException(String.format("Cannot determine domain "
                + "domain type of array type '%s'.", arrayType.toString()));
    }

    /**
     * Return the class defined by the map generic key parameter.
     *
     * @param type map type
     * @return class defined by the map generic parameter for the key
     * @throws InvalidModelException if the type does not define a key generic
     * parameter
     *
     */
    protected static Class<?> getMapKeyClass(Type type)
            throws InvalidModelException {
        if (!(type instanceof ParameterizedType)
                || ((ParameterizedType) type).getActualTypeArguments().length != 2) {
            throw new InvalidModelException(String.format("Type '%s' does not"
                    + "defined generic parameters for a map.", type.toString()));
        }
        return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    /**
     * Return the class defined by the map generic value parameter.
     *
     * @param type map type
     * @return class defined by the generic parameter for the value
     * @throws InvalidModelException if the type does not define a value generic
     * parameter
     */
    protected static Class<?> getMapValueClass(Type type)
            throws InvalidModelException {
        if (!(type instanceof ParameterizedType)
                || ((ParameterizedType) type).getActualTypeArguments().length != 2) {
            throw new RuntimeException();
        }
        return (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
    }

//    
//    
//    protected static MapClassRef<?, ?> mapReference(
//            String refName, String refDesc, Type type)
//            throws InvalidModelException {
//
//        //TODO support annotation to override the key/value metadata
//        NamedRef keyRef
//                = ReflectedTypeRef.typeReference("key",
//                        "",
//                        DomainReflectionUtil.getMapKeyClass(type));
//        if (!TypeClassRef.class.isAssignableFrom(keyRef.getClass())) {
//            //probably another map, currently not supported
//            //TODO add support for nested maps (probably not as a key...)
//            throw new InvalidModelException(String.format("Map key must be a "
//                    + "class representation of a domain type, not a '%s' type",
//                    keyRef.getClass().getName()));
//        }
//
//        NamedRef valueRef
//                = ReflectedTypeRef.typeReference("value",
//                        "",
//                        DomainReflectionUtil.getMapValueClass(type));
//        if (!TypeClassRef.class.isAssignableFrom(valueRef.getClass())) {
//            //probably another map, currently not supported
//            //TODO add support for nested maps
//            throw new InvalidModelException(String.format("Map value must be a "
//                    + "class representation of a domain type, not a '%s' type",
//                    valueRef.getClass().getName()));
//        }
//
//        return new MapClassRef(refName, refDesc,
//                (TypeClassRef) keyRef, (TypeClassRef) valueRef);
//    }
//    
//    protected static NamedRef typeReference(
//            String refName, String refDesc, Type type)
//            throws InvalidModelException {
//        final Class<?> refClass;
//
//        boolean isCollection = DomainReflectionUtil.isCollection(type);
//        if (isCollection) {
//            if (DomainReflectionUtil.isArray(type)) {
//                refClass = DomainReflectionUtil.getArrayClass(type);
//            } else if (DomainReflectionUtil.isCollection(type)) {
//                refClass = DomainReflectionUtil.getCollectionClass(type);
//            } else if (DomainReflectionUtil.isMap(type)) {
//                return mapReference(refName, refDesc, type);
//            } else {
//                throw new InvalidModelException(String.format("Unknown collection"
//                        + "type '%s', unable to determine the domain type for "
//                        + "reference '%s'",
//                        type.toString(), refName));
//            }
//        } else if (type instanceof Class) {
//            refClass = (Class) type;
//        } else if (type instanceof ParameterizedType) {
//            refClass = (Class) ((ParameterizedType) type).getRawType();
//        } else {
//            throw new InvalidModelException(String.format("Unknown type '%s', "
//                    + "unable to determine the domain type for reference '%s'",
//                    type.toString(), refName));
//        }
//
//        final ReflectedTypeClass<?> refModel
//                = ClassTypeReflector.model(refClass);
//
//        return new ReflectedTypeRef(refName, refDesc, refModel, isCollection);
//    }
}
