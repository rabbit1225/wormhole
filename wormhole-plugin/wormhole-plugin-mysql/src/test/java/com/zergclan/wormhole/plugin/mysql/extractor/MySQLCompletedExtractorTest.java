/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zergclan.wormhole.plugin.mysql.extractor;

import com.zergclan.wormhole.metadata.core.plan.node.DataNodeMetaData;
import com.zergclan.wormhole.metadata.core.plan.node.DataNodeTypeMetaData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class MySQLCompletedExtractorTest {
    
    private static MySQLCompletedExtractor extractor;
    
    @BeforeAll
    public static void init() {
        extractor = new MySQLCompletedExtractor();
    }
    
    @Test
    public void assertGeneratorExtractSQl() {
        String expectedSQL = "SELECT t_user.`id` AS `id`,t_user.`name` AS `name` FROM t_user WHERE id = 1";
        String table = "t_user";
        Map<String, DataNodeMetaData> dataNodes = new LinkedHashMap<>();
        dataNodes.put("id", createDataNodeMetaData(table, "id", "INT"));
        dataNodes.put("name", createDataNodeMetaData(table, "name", "TEXT"));
        assertEquals(expectedSQL, extractor.generatorExtractSQl("t_user", "id = 1", dataNodes));
    }
    
    private DataNodeMetaData createDataNodeMetaData(final String table, final String column, final String type) {
        return new DataNodeMetaData(table, column, new DataNodeTypeMetaData("REQUIRED", type));
    }
}
