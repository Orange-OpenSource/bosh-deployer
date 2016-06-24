/*
 * Copyright (c) 2008-2016, Orange, Inc. All Rights Reserved.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import com.orange.oss.bosh.deployer.cfbroker.db.BindingRepository;
import com.orange.oss.bosh.deployer.cfbroker.db.InstanceBinding;
import com.orange.oss.bosh.deployer.cfbroker.db.ServiceRepository;

/**
 * Service Instance Binding broker API
 */
@Service
public class DeployerServiceInstanceBindingService implements ServiceInstanceBindingService {

	@Autowired
    private ServiceRepository serviceRepository;
	
	@Autowired
    private BindingRepository bindingRepository;
	


    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest
                req)  {

        InstanceBinding instanceBinding = bindingRepository.findOne(req.getServiceInstanceId());
        if (instanceBinding != null) {
            throw new IllegalArgumentException("already exist:"+instanceBinding.toString());
        }

//        Map<String, Object> credentials = new HashMap<String, Object>();
//        credentials.put("host", serviceInstance.getHazelcastIPAddress());
//        credentials.put("InetAddress", serviceInstance.getHazelcastInetAddress());
//        credentials.put("Port", serviceInstance.getHazelcastPort());
//
        String appGuid = req.getAppGuid();

        instanceBinding = new InstanceBinding(req.getBindingId(),req.getServiceInstanceId());
        
        this.bindingRepository.save(instanceBinding);
        
        //TODO: 
        // generate dedicated credentials for binding (optional, otherwhise return service instance global credentials)
        // returns connectivity name
        
        return new CreateServiceInstanceBindingResponse();
        
    }

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest
           deleteServiceInstanceBindingRequest) {
        String id = deleteServiceInstanceBindingRequest.getBindingId();

        InstanceBinding instanceBinding = this.bindingRepository.findOne(id);
        if (instanceBinding != null) {
            this.bindingRepository.delete(instanceBinding);
        }
    }
}
