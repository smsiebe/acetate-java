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
package org.geoint.acetate.java.event;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.geoint.acetate.EventInstance;

/**
 * Implements the EventHandler management by using {@link WeakReference weak}
 * references to the handlers, "unsubscribing" the handlers when only weak
 * references to the handlers are left.
 * <p>
 * Subclasses can notify any registered/non-reclaimed event handlers by calling
 * one of the #publishEvent methods.
 * <p>
 * This implementation publishes to the handlers on one thread, synchronously.
 * Subclasses may override the {@link PublishEvent(EventInstance)} method to
 * change this behavior.
 *
 * @author steve_siebert
 */
public abstract class WeakSubscriptionEventSource implements EventSource {
    
    private final Set<WeakReference<EventHandler>> handlers
            = Collections.synchronizedSet(new HashSet<>());
    
    @Override
    public void subscribe(EventHandler handler) {
        handlers.add(new WeakReference(handler));
    }

    /**
     * Publishes the event to remaining (those not reclaimed by the JVM)
     * subscribed handlers.
     *
     * @param event event to publish
     */
    protected void publishEvent(EventInstance event) {
        synchronized (this) {
            Iterator<WeakReference<EventHandler>> iterator
                    = handlers.iterator();
            while (iterator.hasNext()) {
                
                EventHandler h = iterator.next().get();
                
                if (h == null) {
                    iterator.remove(); //remove from handler collection
                    continue;
                }
                
                h.handle(event);
            }
        }
    }

    //move this to acetate-java
//    /**
//     * Uses the {@link DomainRegistry#DEFAULT default registry} to resolve the
//     * event model, then publishes the event by calling 
//     * {@link WeakSubscriptionEventSource#publishEvent(EventInstance) }.
//     * <p>
//     * Subclasses wishing to change the way an event object domain is resolved
//     * need only override this method.
//     *
//     * @param event java object representation of a domain event
//     * @throws NotDomainTypeException if the model could not be determined for
//     * the object
//     * @throws InvalidModelException if the object represents a domain type
//     * other than an EventType
//     */
//    protected void publishEvent(Object event)
//            throws NotDomainTypeException, InvalidModelException {
//
//    }
}
