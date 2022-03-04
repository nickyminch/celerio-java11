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

package com.jaxio.celerio.configuration.database.support;

import java.sql.ResultSet;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ResultSetTables extends ResultSetWrapper {

    private static Map<String, Integer> colNums = newHashMap();

    static {
        colNums.put("TABLE_CAT", 1);
        colNums.put("TABLE_SCHEM", 2);
        colNums.put("TABLE_NAME", 3);
        colNums.put("TABLE_TYPE", 4);
        colNums.put("REMARKS", 5);
        colNums.put("TYPE_CAT", 6);
        colNums.put("TYPE_SCHEM", 7);
        colNums.put("TYPE_NAME", 8);
        colNums.put("SELF_REFERENCING_COL_NAME", 9);
        colNums.put("REF_GENERATION", 10);
    }

    @Override
    protected int toInt(String label) {
        return colNums.get(label);
    }

    public ResultSetTables(ResultSet rs, boolean useLabel) {
        super(rs, useLabel);
    }
}
