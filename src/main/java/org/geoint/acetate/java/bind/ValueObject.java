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
package org.geoint.acetate.java.bind;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import org.geoint.acetate.DomainInstantiationException;
import org.geoint.acetate.ValueInstance;
import org.geoint.acetate.format.Format;
import org.geoint.acetate.format.TypeFormatter;
import org.geoint.acetate.format.TypeFormattingException;
import org.geoint.acetate.format.TypeParser;
import org.geoint.acetate.java.format.ObjectFormatter;
import org.geoint.acetate.java.format.ObjectParser;
import org.geoint.acetate.model.ValueType;

/**
 *
 * @author steve_siebert
 * @param <T>
 */
public class ValueObject<T> implements ValueInstance, TypeObject<T, ValueType> {

    private final ValueType model;
    private final T obj;
    private final Supplier<ObjectFormatter> defaultBinFormatter;
    private final Supplier<ObjectParser> defaultBinParser;

    public ValueObject(ValueType model, T obj,
            Supplier<ObjectFormatter> defaultBinFormatter,
            Supplier<ObjectParser> defaultBinParser) {
        this.model = model;
        this.obj = obj;
        this.defaultBinFormatter = defaultBinFormatter;
        this.defaultBinParser = defaultBinParser;
    }

    @Override
    public T asObject() {
        return obj;
    }

    @Override
    public byte[] asBytes() throws DomainInstantiationException {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            defaultBinFormatter.get().format(Format.GENERIC_BINARY_FORMAT, obj, this, bout);
            return bout.toByteArray();
        } catch (IOException ex) {
            throw new TypeFormattingException("Unable to format value object "
                    + "in default binary format.", ex);
        }
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    @Override
    public ValueType getModel() {
        return model;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.model);
        hash = 37 * hash + Objects.hashCode(this.obj);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValueObject<?> other = (ValueObject<?>) obj;
        if (!Objects.equals(this.model, other.model)) {
            return false;
        }
        return Objects.equals(this.obj, other.obj);
    }

}
