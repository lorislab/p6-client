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

class MessageProperties {

    static final String QUEUE_REQUEST = "p6.request";

    static final String QUEUE_RESPONSE = "p6.response";

    static final String QUEUE_CMD = "p6.cmd";

    static final String MSG_APP_NAME = "P6_APP_NAME";

    static final String MSG_MODULE_NAME = "P6_MODULE_NAME";

    static final String MSG_RESOURCE_PATH = "P6_RESOURCE_PATH";

    static final String DEPLOYMENT_DESCRIPTOR = "/p6.properties";

    static final String MSG_CMD = "P6_CMD";

    static final String MSG_PROCESS_TOKEN_SERVICE_TASK = "P6_PROCESS_TOKEN_SERVICE_TASK";

    static final String MSG_PROCESS_TOKEN_ID = "P6_PROCESS_TOKEN_ID";

    static final String MSG_PROCESS_ID = "P6_PROCESS_ID";

    static final String MSG_PROCESS_INSTANCE_ID = "P6_PROCESS_INSTANCE_ID";

    static final String MSG_PROCESS_VERSION = "P6_PROCESS_VERSION";

    static final String MSG_PROCESS_EVENT_ID = "MSG_PROCESS_EVENT_ID";

    static final String MSG_PROCESS_MESSAGE_ID = "MSG_PROCESS_MESSAGE_ID";

    static final String CMD_DEPLOY = "CMD_DEPLOY";

    static final String CMD_START_PROCESS = "CMD_START_PROCESS";

    static final String CMD_SEND_MESSAGE = "CMD_SEND_MESSAGE";

    static final String DATA_KEY_EVENT_DATA = "P6_EVENT_DATA";

}
