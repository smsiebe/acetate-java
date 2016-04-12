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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import org.geoint.acetate.TypeInstance;
import org.geoint.acetate.format.DomainFormatException;
import org.geoint.acetate.format.Format;
import org.geoint.acetate.format.FormattedType;
import org.geoint.acetate.format.TypeFormat;
import org.geoint.acetate.format.TypeFormattingException;
import org.geoint.acetate.java.format.ObjectFormatter;
import org.geoint.acetate.java.format.ObjectParser;
import org.geoint.acetate.java.model.MockValidValue.MockValidValueBinaryFormatter;
import org.geoint.acetate.java.model.MockValidValue.MockValidValueBinaryParser;
import org.geoint.acetate.model.DomainType;

/**
 * Mock value with valid annotations.
 * <p>
 * Simply an int with a default value.
 *
 * @author steve_siebert
 */
@DomainValue(namespace = MockDomainConstants.MOCK_NAMESPACE,
        version = MockDomainConstants.MOCK_VERSION,
        type = "MockOperationResponse",
        defaultFormatter = MockValidValueBinaryFormatter.class,
        defaultParser = MockValidValueBinaryParser.class)
public class MockValidValue {

    public static final int DEFAULT_VALUE = 97;
    private final int intValue;

    public MockValidValue() {
        this(DEFAULT_VALUE);
    }

    public MockValidValue(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public static class MockValidValueBinaryFormatter implements ObjectFormatter<MockValidValue> {

        @Override
        public boolean supports(TypeFormat format) {
            return true;
        }

        @Override
        public void format(Format format, MockValidValue obj,
                TypeInstance instance, OutputStream out)
                throws IOException, TypeFormattingException {
            try (DataOutputStream dout = new DataOutputStream(out)) {
                dout.writeInt(obj.intValue);
            }
        }

    }

    public static class MockValidValueBinaryParser implements ObjectParser<MockValidValue> {

        @Override
        public boolean supports(TypeFormat typeFormat) {
            return true;
        }

        @Override
        public MockValidValue parse(DomainType type, FormattedType formatted)
                throws DomainFormatException, IOException {
            try (DataInputStream din = new DataInputStream(formatted.getFormatted())) {
                return new MockValidValue(din.readInt());
            } catch (EOFException ex) {
                return new MockValidValue();
            }
        }

    }
}
