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
import org.lorislab.p6.annotations.ServiceTask;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.inject.Inject;
import javax.jms.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@MessageDriven(name = "ClientExecutorService",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + MessageProperties.QUEUE_REQUEST),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class ClientExecutorService implements MessageListener {

    @Inject
    private BeanManager bm;

    @Inject
    private JMSContext context;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {

        TextMessage response = null;

        try {

            String processId = message.getStringProperty(MessageProperties.MSG_PROCESS_ID);
            String processVersion = message.getStringProperty(MessageProperties.MSG_PROCESS_VERSION);
            String processInstanceId = message.getStringProperty(MessageProperties.MSG_PROCESS_INSTANCE_ID);
            String tokenId = message.getStringProperty(MessageProperties.MSG_PROCESS_TOKEN_ID);
            String serviceTask = message.getStringProperty(MessageProperties.MSG_PROCESS_TOKEN_SERVICE_TASK);


            log.info("Start value {} {} {} {} {}", processId, processVersion, processInstanceId, tokenId, serviceTask);


            // find the bean and method to call.
            ObserverMethod<? super ServiceTaskItem> observer = null;
            Set<ObserverMethod<? super ServiceTaskItem>> observers = bm.resolveObserverMethods(new ServiceTaskItem(), new ServiceTask.Literal(processId, processVersion,serviceTask));
            if (!observers.isEmpty()) {

                Iterator<ObserverMethod<? super ServiceTaskItem>> iter = observers.iterator();
                observer = iter.next();
                if (observers.size() > 1) {
                    log.warn("Multiple service tasks implementations! For service task: {} {} {}", processId, processVersion, serviceTask);
                }
            }

            if (observer == null) {
                log.warn("Missing implementation for the process {}/{} and service task {}", processId, processVersion, serviceTask);
            } else {
                Map<String, Object> data = null;
                String txt = message.getBody(String.class);
                log.info("Data: {}", txt);

                ServiceTaskItem item = new ServiceTaskItem();
                item.setProcessId(processId);
                item.setProcessVersion(processVersion);
                item.setProcessInstanceId(processInstanceId);
                item.setTokenId(tokenId);
                item.setParameters(ClientJsonService.fromString(txt));
                item.setServiceTaskName(serviceTask);

                observer.notify(item);

                response = context.createTextMessage();

                // check if the method is not async and get the results.
                if (!observer.isAsync()) {
                    String tmp = ClientJsonService.toString(item.getResults());
                    response.setText(tmp);
                }

                response.setStringProperty(MessageProperties.MSG_PROCESS_ID, processId);
                response.setStringProperty(MessageProperties.MSG_PROCESS_VERSION, processVersion);
                response.setStringProperty(MessageProperties.MSG_PROCESS_INSTANCE_ID, processInstanceId);
                response.setStringProperty(MessageProperties.MSG_PROCESS_TOKEN_ID, tokenId);
                response.setStringProperty(MessageProperties.MSG_PROCESS_TOKEN_SERVICE_TASK, serviceTask);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error executeGateway service task", ex);
        }

        // send message back to the process
        if (response != null) {
            Queue queue = context.createQueue(MessageProperties.QUEUE_RESPONSE);
            context.createProducer().send(queue, response);
        } else {
            log.error("No response message to send back to the engine.");
        }
    }

}
