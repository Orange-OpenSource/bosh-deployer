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
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.orange.oss.bosh.deployer.BoshClient;
import com.orange.oss.bosh.deployer.cfbroker.db.Instance;
import com.orange.oss.bosh.deployer.cfbroker.db.ServiceRepository;
/**
 * Service Instance broker API
 */

@Component
public class DeployerServiceInstanceService implements ServiceInstanceService  {


	@Autowired
    private ServiceRepository serviceRepository;
	
    @Autowired
    BoshClient boshClient;
    
    
    
    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest req)
             {
        String instanceId = req.getServiceInstanceId();
        
        //TODO: asynchronously launch bosh deployment provisionnig
        

        Instance serviceInstance = this.serviceRepository.findOne(instanceId);
        if (serviceInstance != null) {
            throw new ServiceInstanceExistsException("error already exists",serviceInstance.toString());
        }

        serviceInstance = new Instance(req.getServiceInstanceId());
        

//        HazelcastInstance hazelcastInstance = hazelcastAdmin.createHazelcastInstance(
//                createServiceInstanceRequest.getServiceInstanceId());
//        
//        if (hazelcastInstance == null) {
//            throw new DeployerServiceException("Failed to create new Hazelcast member hazelcastInstance: "
//                    + createServiceInstanceRequest.getServiceInstanceId());
//        }

//        
//        String hazelcastHost = hazelcastInstance.getCluster().getLocalMember().getAddress().getHost();
//        ((DeployerServiceInstance) serviceInstance).setHazelcastIPAddress(hazelcastHost);
//
//        int hazelcastPort = hazelcastInstance.getCluster().getLocalMember().getAddress().getPort();
//        ((DeployerServiceInstance) serviceInstance).setHazelcastPort(hazelcastPort);
//
//        try {
//            InetAddress hazelcastInetAddress = hazelcastInstance.getCluster().getLocalMember().getAddress().getInetAddress();
//            ((DeployerServiceInstance) serviceInstance).setHazelcastInetAddress(hazelcastInetAddress);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        
        this.serviceRepository.save(serviceInstance);
        CreateServiceInstanceResponse response=new CreateServiceInstanceResponse().withAsync(true);
        return response;
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest deleteServiceInstanceRequest){
    	
    	//Asynchronously delete related bosh deployment implementing the service instance
        Instance serviceInstance = this.serviceRepository.findOne(deleteServiceInstanceRequest.getServiceInstanceId());
        
        if (serviceInstance==null){
        	throw new  ServiceInstanceDoesNotExistException("Service Instance unknow");
        }
        this.serviceRepository.delete(serviceInstance);
        
        //FIXME launch delete (async)
        //hazelcastAdmin.deleteHazelcastInstance(deleteServiceInstanceRequest.getServiceInstanceId());
        return new DeleteServiceInstanceResponse().withAsync(true);
    }

    @Override
    public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest updateServiceInstanceRequest){
        
    	//TODO: check update. if plan changed implying sizing, must redeploy bosh deployment asynchronously
    	Instance serviceInstance = this.serviceRepository.findOne(updateServiceInstanceRequest.getServiceInstanceId());
        if (serviceInstance == null) {
            throw new ServiceInstanceDoesNotExistException(updateServiceInstanceRequest.getServiceInstanceId());
        }
        
        //FIXME: change and update
        this.serviceRepository.save(serviceInstance);
        
        return new UpdateServiceInstanceResponse();
    }



	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest req) {
		String serviceInstanceId=req.getServiceInstanceId();
        Instance serviceInstance = this.serviceRepository.findOne(req.getServiceInstanceId());
        if (serviceInstance==null){
        	throw new  ServiceInstanceDoesNotExistException("Service Instance unknow");
        }
		
		
		
		OperationState state=OperationState.IN_PROGRESS;
		//TODO check id -> succeeded or failed.
		//this is the polling from cf expecting async provisionning to transition to finished or error state
		return new GetLastServiceOperationResponse().withOperationState(state);
	}

}
