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

import org.geoint.acetate.OperationInstance;

/**
 * Provides domain meta for a method-defined resource operation.
 *
 * @author steve_siebert
 * @param <R> java class representation of a domain resource
 * @param <E> java class representation of the event
 */
public interface OperationMethod<R, E>
        extends OperationInstance<ResourceObject<R>, EventObject<E>> {

}