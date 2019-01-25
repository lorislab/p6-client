/*
 * Copyright 2019 lorislab.org.
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
package org.lorislab.p6.client.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceTaskItem {

    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    private String processId;

    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    private String processVersion;

    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    private String processInstanceId;

    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    private String serviceTaskName;

    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    private String tokenId;

    @Setter(value = AccessLevel.PACKAGE)
    private Map parameters = new HashMap<>();

    @Getter(value = AccessLevel.PACKAGE)
    private Map<String, Object> results = new HashMap<>();

    @SuppressWarnings("unchecked")
    public Set<String> getParameterNames() {
        return new HashSet<String>(parameters.keySet());
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name) {
        return (T) parameters.get(name);
    }

    public <T> T getParameter(String name, Class<T> clazz) {
        Object value = parameters.get(name);
        return ClientJsonService.fromValue(value, clazz);
    }

    public void addResult(String name, Object value) {
        results.put(name, value);
    }

    public void addResults(Map<String, Object> data) {
        if (data != null) {
            results.putAll(data);
        }
    }

}
