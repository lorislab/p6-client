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

import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientJsonServiceTest {

    @Test
    public void jsonTest() {
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);
        Jsonb json = JsonbBuilder.create(config);

        Map<String, Object> data = new HashMap<>();
        data.put("key1", "1234");
        data.put("key2", 1234);
        data.put("key3", new ArrayList<String>(Arrays.asList("1","2")));

        String text = json.toJson(data);
        System.out.println(text);

        Map object = json.fromJson(text, Map.class);

        System.out.println(object);
        System.out.println(object.getClass().getName());

        Map<String, Object> map = (Map<String, Object>) object;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue() + " " + entry.getValue().getClass().getName());
        }
    }

    @Test
    public void serilizeExampleTest() {

        Map<String, Object> data = new HashMap<>();
        data.put("key1", "1234");
        data.put("key2", 1234);
        data.put("key3", new ArrayList<String>(Arrays.asList("1","2")));
        data.put("key4", null);

        String tmp2 = ClientJsonService.toString(data);
        System.out.println(tmp2);

    }
}
