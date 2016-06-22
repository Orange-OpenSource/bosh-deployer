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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.ServiceInstance;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.orange.oss.bosh.deployer.BoshClient;
/**
 * HazelcastServiceInstanceService class
 */

@Service
public class DeployerServiceInstanceService implements ServiceInstanceService {

    private static DeployerServiceRepository repository = DeployerServiceRepository.getInstance();
    private HazelcastAdmin hazelcastAdmin;

    @Autowired
    public DeployerServiceInstanceService(HazelcastAdmin hazelcastAdmin) {
        this.hazelcastAdmin = hazelcastAdmin;
    }

    @Autowired
    BoshClient boshClient;
    
    
    
    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest createServiceInstanceRequest)
             {
        String instanceId = createServiceInstanceRequest.getServiceInstanceId();
        
        //TODO: asynchronously launch bosh deployment provisionnig
        

        ServiceInstance serviceInstance = repository.findServiceInstance(instanceId);
        if (serviceInstance != null) {
            throw new ServiceInstanceExistsException("error already exists",serviceInstance.toString());
        }

        serviceInstance = new DeployerServiceInstance(createServiceInstanceRequest);

        HazelcastInstance hazelcastInstance = hazelcastAdmin.createHazelcastInstance(
                createServiceInstanceRequest.getServiceInstanceId());
        if (hazelcastInstance == null) {
            throw new DeployerServiceException("Failed to create new Hazelcast member hazelcastInstance: "
                    + createServiceInstanceRequest.getServiceInstanceId());
        }

        String hazelcastHost = hazelcastInstance.getCluster().getLocalMember().getAddress().getHost();
        ((DeployerServiceInstance) serviceInstance).setHazelcastIPAddress(hazelcastHost);

        int hazelcastPort = hazelcastInstance.getCluster().getLocalMember().getAddress().getPort();
        ((DeployerServiceInstance) serviceInstance).setHazelcastPort(hazelcastPort);

        try {
            InetAddress hazelcastInetAddress = hazelcastInstance.getCluster().getLocalMember().getAddress().getInetAddress();
            ((DeployerServiceInstance) serviceInstance).setHazelcastInetAddress(hazelcastInetAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        repository.saveServiceInstance(serviceInstance);
        CreateServiceInstanceResponse response=new CreateServiceInstanceResponse();
        
        
        return new CreateServiceInstanceResponse();
    }


    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest deleteServiceInstanceRequest){
    	
    	
    	String serviceInstanceId=deleteServiceInstanceRequest.getServiceInstanceId();
    	
    	//synchronously delete related bosh deployment implementing the service instance
    	
        ServiceInstance serviceInstance = repository.findServiceInstance(
                deleteServiceInstanceRequest.getServiceInstanceId());
        if (serviceInstance != null) {
            repository.deleteServiceInstance(serviceInstance);
            hazelcastAdmin.deleteHazelcastInstance(deleteServiceInstanceRequest.getServiceInstanceId());
        }
        return new DeleteServiceInstanceResponse();
    }

    @Override
    public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest updateServiceInstanceRequest){
        
    	//TODO: check update. if plan changed implying sizing, must redeploy bosh deployment asynchronously
    	
    	
    	ServiceInstance serviceInstance = repository.findServiceInstance(updateServiceInstanceRequest.getServiceInstanceId());
        if (serviceInstance == null) {
            throw new ServiceInstanceDoesNotExistException(updateServiceInstanceRequest.getServiceInstanceId());
        }
        repository.deleteServiceInstance(serviceInstance);

        ServiceInstance updatedServiceInstance = new ServiceInstance(updateServiceInstanceRequest);
        repository.saveServiceInstance(updatedServiceInstance);
        
        
        return new UpdateServiceInstanceResponse();
    }



	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest req) {
		
		String serviceInstanceId=req.getServiceInstanceId();
		
		//TODO check id -> succeeded or failed.
		//this is the polling from cf expecting async provisionning to transition to finished or error state
		return new GetLastServiceOperationResponse();
	}

}
