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

package com.jaxio.celerio.configuration.validation;

import org.hibernate.validator.HibernateValidator;

import javax.validation.*;
import java.lang.annotation.ElementType;

public class ValidatorUtil {
    public static Validator getValidator() {
        Configuration<?> configuration = Validation.byProvider(HibernateValidator.class).configure();
        configuration.traversableResolver(new DummyTraversableResolver());
        return configuration.buildValidatorFactory().getValidator();
    }

    public static class DummyTraversableResolver implements TraversableResolver {
        public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject,
                                   ElementType elementType) {
            return true;
        }

        public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject,
                                    ElementType elementType) {
            return true;
        }
    }
}
