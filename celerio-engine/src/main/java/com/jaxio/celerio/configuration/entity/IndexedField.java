/*
 * Copyright 2015 JAXIO http://www.jaxio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaxio.celerio.configuration.entity;

import lombok.Data;
import repackaged.org.hibernate.search.annotations.Analyze;
import repackaged.org.hibernate.search.annotations.Norms;
import repackaged.org.hibernate.search.annotations.Store;
import repackaged.org.hibernate.search.annotations.TermVector;

import static org.springframework.util.StringUtils.hasLength;

/*
 * Allows you to configure Hibernate Search Field annotation.
 */
@Data
public class IndexedField {
    public String name;
    public Store store;
    public Boolean index;
    public Analyze analyze;
    public String analyzer;
    public Norms norms;
    public TermVector termVector;
    public String bridgeImpl;

    /*
     * The full type of the field bridge implementation to use. When not null it is used by the @FieldBridge annotation.
     */
    public String getBridgeImpl() {
        return bridgeImpl;
    }

    public boolean hasBridgeImpl() {
        return hasLength(bridgeImpl);
    }
}