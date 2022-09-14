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

package com.zergclan.wormhole.common.metadata.authorization;

import com.zergclan.wormhole.common.WormholeMetaData;
import com.zergclan.wormhole.common.metadata.authorization.user.WormholeUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * Authorization metadata.
 */
@RequiredArgsConstructor
@Getter
public final class AuthorizationMetaData implements WormholeMetaData {
    
    private final String identifier = "wormhole-authorization";
    
    private final Map<String, WormholeUser> users;
    
    /**
     * Get user.
     *
     * @param username username
     * @return {@link WormholeUser}
     */
    public Optional<WormholeUser> getUser(final String username) {
        return Optional.ofNullable(users.get(username));
    }
}
