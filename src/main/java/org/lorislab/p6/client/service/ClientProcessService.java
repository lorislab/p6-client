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

import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ClientProcessService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    public String startProcess(String processId, Map<String, Object> data) throws Exception {
        String processInstanceId = UUID.randomUUID().toString();

        TextMessage msg = context.createTextMessage();
        msg.setStringProperty(MessageProperties.MSG_CMD, MessageProperties.CMD_START_PROCESS);
        msg.setStringProperty(MessageProperties.MSG_PROCESS_INSTANCE_ID, processInstanceId);
        msg.setStringProperty(MessageProperties.MSG_PROCESS_ID, processId);
        sendMessage(msg, data);

        return processInstanceId;
    }

    public String sendMessage(String processInstanceId, Map<String, Object> data) throws Exception {
        String messageId = UUID.randomUUID().toString();

        TextMessage msg = context.createTextMessage();
        msg.setStringProperty(MessageProperties.MSG_CMD, MessageProperties.CMD_SEND_MESSAGE);
        msg.setStringProperty(MessageProperties.MSG_PROCESS_INSTANCE_ID, processInstanceId);
        msg.setStringProperty(MessageProperties.MSG_PROCESS_MESSAGE_ID, messageId);
        sendMessage(msg, data);

        return messageId;
    }


    public String sendEvent(String processInstanceId, Object data) throws Exception {
        String eventId = UUID.randomUUID().toString();

        TextMessage msg = context.createTextMessage();
        msg.setStringProperty(MessageProperties.MSG_CMD, MessageProperties.CMD_SEND_MESSAGE);
        msg.setStringProperty(MessageProperties.MSG_PROCESS_INSTANCE_ID, processInstanceId);
        msg.setStringProperty(MessageProperties.MSG_PROCESS_EVENT_ID, eventId);

        Map<String, Object> tmp = Map.of(MessageProperties.DATA_KEY_EVENT_DATA, data);
        sendMessage(msg, tmp);

        return eventId;
    }

    private void sendMessage(TextMessage msg, Map<String, Object> data) throws Exception {
        if (data != null) {
            String content = ClientJsonService.toString(data);
            msg.setText(content);
        }
        Queue queue = context.createQueue(MessageProperties.QUEUE_CMD);
        context.createProducer().send(queue, msg);
    }
}
