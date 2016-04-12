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
import java.io.OutputStream;
import org.geoint.acetate.TypeInstance;
import org.geoint.acetate.format.Format;
import org.geoint.acetate.format.TypeFormat;
import org.geoint.acetate.format.TypeFormattingException;

/**
 *
 * @author steve_siebert
 */
public interface ObjectFormatter<T> {

    boolean supports(TypeFormat format);

    void format(Format format, T obj, TypeInstance instance, OutputStream out)
            throws IOException, TypeFormattingException;
}
