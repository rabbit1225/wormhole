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

package com.zergclan.wormhole.pipeline.validator;

import com.zergclan.wormhole.common.data.node.StringDataNode;
import com.zergclan.wormhole.common.data.node.WormholeDataNode;
import com.zergclan.wormhole.common.data.node.type.DataNodeType;
import com.zergclan.wormhole.common.exception.WormholeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RequiredValidatorTest {
    
    private static WormholeValidator<WormholeDataNode<?>> wormholeValidator;
    
    @BeforeAll
    public static void init() {
        wormholeValidator = new RequiredValidator();
    }
    
    @Test
    public void assertValidate() {
        StringDataNode stringDataNode = new StringDataNode("column", "column comment", DataNodeType.NATIVE);
        WormholeException exception = assertThrows(WormholeException.class, () -> wormholeValidator.validate(stringDataNode));
        assertEquals("Required value can not be null", exception.getMessage());
    }
}
