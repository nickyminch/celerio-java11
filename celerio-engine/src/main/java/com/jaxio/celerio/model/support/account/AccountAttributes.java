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

package com.jaxio.celerio.model.support.account;

import com.jaxio.celerio.model.Attribute;
import com.jaxio.celerio.model.Relation;
import lombok.Data;

@Data
public class AccountAttributes {
    private Attribute username;
    private Attribute password;
    private Attribute email;
    private Attribute enabled;
    private Relation roleRelation;

    public boolean isUsernameSet() {
        return username != null;
    }

    public boolean isPasswordSet() {
        return password != null;
    }

    public boolean isEmailSet() {
        return email != null;
    }

    public boolean isEnabledSet() {
        return enabled != null;
    }

    public boolean isRoleRelationSet() {
        return roleRelation != null;
    }
}
