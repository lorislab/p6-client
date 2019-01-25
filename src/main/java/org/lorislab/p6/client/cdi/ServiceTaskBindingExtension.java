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
package org.lorislab.p6.client.cdi;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.annotations.ServiceTask;
import org.lorislab.p6.annotations.WorkflowProcess;
import org.lorislab.p6.client.service.ServiceTaskItem;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.inject.spi.configurator.AnnotatedMethodConfigurator;
import javax.enterprise.inject.spi.configurator.AnnotatedParameterConfigurator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

@Slf4j
public class ServiceTaskBindingExtension implements Extension {


    private static final Observes annotation = new Observes() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Observes.class;
        }

        @Override
        public Reception notifyObserver() {
            return Reception.ALWAYS;
        }

        @Override
        public TransactionPhase during() {
            return TransactionPhase.IN_PROGRESS;
        }

    };

    <T> void processServiceTaskS(@Observes @WithAnnotations({ServiceTask.class}) ProcessAnnotatedType<T> processAnnotatedType) {

        String defaultValue = "";
        String defaultProcessId = "";
        String defaultProcessVersion = "";

        WorkflowProcess wp = processAnnotatedType.getAnnotatedType().getAnnotation(WorkflowProcess.class);
        if (wp != null) {
            defaultProcessId = wp.processId();
            defaultProcessVersion = wp.processVersion();
        }

        Set<AnnotatedMethodConfigurator<? super T>> methods = processAnnotatedType.configureAnnotatedType().methods();
        if (methods != null) {
            for (AnnotatedMethodConfigurator<? super T> method : methods) {

                Method javaMethod = method.getAnnotated().getJavaMember();

                ServiceTask st = method.getAnnotated().getAnnotation(ServiceTask.class);
                if (st != null) {
                    defaultValue = st.value();
                    if (!st.processId().isEmpty()) {
                        defaultProcessId = st.processId();
                    }
                    if (!st.processVersion().isEmpty()) {
                        defaultProcessVersion = st.processVersion();
                    }
                }

                if (method.params() != null) {
                    for (AnnotatedParameterConfigurator parameter : method.params()) {

                        AnnotatedParameter ap = parameter.getAnnotated();
                        ServiceTask anno = ap.getAnnotation(ServiceTask.class);

                        if ((anno != null || st != null) && ap.getJavaParameter().getType().isAssignableFrom(ServiceTaskItem.class)) {

                            String value = defaultValue;
                            String processId = defaultProcessId;
                            String processVersion = defaultProcessVersion;

                            // parameter annotation value
                            if (anno != null) {
                                value = anno.value();
                                if (!anno.processId().isEmpty()) {
                                    processId = anno.processId();
                                }
                                if (!anno.processVersion().isEmpty()) {
                                    processVersion = anno.processVersion();
                                }
                                parameter.remove((a) -> a.equals(anno));
                            }

                            if (processId.isEmpty() || processVersion.isEmpty() || value.isEmpty()) {
                                log.error("No valid configuration for service task: {}.{} {} {} {}", javaMethod.getDeclaringClass().getName(), javaMethod.getName(), processId, processVersion, value);
                                throw new RuntimeException("No valid configuration for service task: " + javaMethod.getDeclaringClass().getName() + "." + javaMethod.getName());
                            }

                            // add the Observers annotation
                            if (!ap.isAnnotationPresent(Observes.class)) {
                                parameter.add(annotation);
                            }

                            // add the service task annotation
                            parameter.add(ServiceTask.Literal.annotation(processId, processVersion, value));
                            log.info("Registre service task: {}.{} {} {} {}", javaMethod.getDeclaringClass().getName(), javaMethod.getName(), processId, processVersion, value);
                        }
                    }
                }
            }
        }
    }

}
