/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.orange.oss.bosh.deployer.cfbroker;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.ServiceInstanceBinding;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * HazelcastServiceInstanceBindingService class
 */
@Service
public class DeployerServiceInstanceBindingService implements ServiceInstanceBindingService {

    private DeployerServiceRepository repository;

    public DeployerServiceInstanceBindingService() {
        repository = DeployerServiceRepository.getInstance();
    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest
                createServiceInstanceBindingRequest)  {
        String id = createServiceInstanceBindingRequest.getBindingId();

        ServiceInstanceBinding instanceBinding = repository.findServiceInstanceBinding(id);
        if (instanceBinding != null) {
            throw new IllegalArgumentException("already exist:"+instanceBinding.toString());
        }

        String serviceInstanceId = createServiceInstanceBindingRequest.getServiceInstanceId();
        DeployerServiceInstance serviceInstance = (DeployerServiceInstance) repository.findServiceInstance(serviceInstanceId);

        Map<String, Object> credentials = new HashMap<String, Object>();
        credentials.put("host", serviceInstance.getHazelcastIPAddress());
        credentials.put("InetAddress", serviceInstance.getHazelcastInetAddress());
        credentials.put("Port", serviceInstance.getHazelcastPort());

        String appGuid = createServiceInstanceBindingRequest.getAppGuid();

        instanceBinding = new ServiceInstanceBinding(id, serviceInstanceId, credentials, null, appGuid);
        repository.saveServiceInstanceBinding(instanceBinding);
        
        //TODO: 
        // generate dedicated credentials for binding (optional, otherwhise return service instance global credentials)
        // returns connectivity name
        
        
        return new CreateServiceInstanceBindingResponse();
        
    }

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest
           deleteServiceInstanceBindingRequest) {
        String id = deleteServiceInstanceBindingRequest.getBindingId();

        ServiceInstanceBinding instanceBinding = repository.findServiceInstanceBinding(id);
        if (instanceBinding != null) {
            repository.deleteServiceInstanceBinding(instanceBinding);
        }
    }
}
