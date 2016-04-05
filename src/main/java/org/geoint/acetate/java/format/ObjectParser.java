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
package org.geoint.acetate.java.format;

import java.io.IOException;
import org.geoint.acetate.model.TypeDescriptor;
import org.geoint.acetate.format.FormattedType;
import org.geoint.acetate.java.TypeObject;
import org.geoint.acetate.model.DomainType;

/**
 *
 * @param <T>
 */
public interface ObjectParser<T> {

    TypeDescriptor[] getSupportedTypes();

    String getSupportedFormat();

    TypeObject<T> parse(DomainType type, FormattedType formatted)
            throws IOException;
}