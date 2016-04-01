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
/**
 * Static java binding to a domain model.
 * <p>
 * A domain model is separate concept from the java class model, exposing a
 * simplified yet specialized representation of a domain. This separation allows
 * a domain to be defined, and managed, independent of the java language and
 * constraints imposed by it and the JVM. This abstraction is beneficial to
 * applications consisting of complex domains, though working with the domain as
 * an abstract concept is cumbersome. This package provides a bridge between the
 * static java language constructs and the domain model, allowing applications
 * to be written in the familiar class/object structure and be deployed with/on
 * domain-aware frameworks and containers.
 * <p>
 * Due to the decoupling of the concept of a {@link DomainTypeModel domain type}
 * and a java class, a domain type may be represented by zero or more different
 * java classes. However, due to the static nature of a java class definition, a
 * class may only represent on domain type. This has important implications for
 * frameworks and applications, allowing, for example, independent domain
 * modeling from java class definitions.
 *
 */
package org.geoint.acetate.java;
