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

package com.zergclan.wormhole.test.integration.framework.data.node;

import com.zergclan.wormhole.tool.constant.MarkConstant;
import com.zergclan.wormhole.tool.util.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Column node.
 */
@RequiredArgsConstructor
@Getter
public final class ColumnNode {
    
    private final String name;
    
    private final String type;
    
    public ColumnNode(final String column) {
        int index = column.indexOf(MarkConstant.COLON);
        Validator.preState(index > 0 && index < column.length() - 2, "Error configuration dataset about column");
        name = column.substring(0, index);
        type = column.substring(index + 1);
    }
}
