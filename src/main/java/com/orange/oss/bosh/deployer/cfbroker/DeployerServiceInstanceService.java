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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.orange.oss.bosh.deployer.ApiMappings.Task;
import com.orange.oss.bosh.deployer.ApiMappings.TaskStatus;
import com.orange.oss.bosh.deployer.BoshClient;
import com.orange.oss.bosh.deployer.cfbroker.db.Instance;
import com.orange.oss.bosh.deployer.cfbroker.db.ServiceRepository;
import com.orange.oss.bosh.deployer.manifest.DeploymentSpec;
import com.orange.oss.bosh.deployer.manifest.DeploymentSpecFactory;
import com.orange.oss.bosh.deployer.manifest.ManifestComposer;
import com.orange.oss.bosh.deployer.manifest.ManifestMapping;
import com.orange.oss.bosh.deployer.manifest.ManifestParser;


/**
 * Service Instance broker API
 */

@Component
public class DeployerServiceInstanceService implements ServiceInstanceService  {

	private static Logger logger=LoggerFactory.getLogger(DeployerServiceInstanceService.class.getName());

	@Autowired
    private ServiceRepository serviceRepository;

	@Autowired
	private ManifestComposer manifestComposer;
	
	@Autowired
	private ManifestParser manifestParser;
	
	@Autowired
	private DeploymentSpecFactory deploymentSpecFactory;
	
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
        
        DeploymentSpec spec=this.deploymentSpecFactory.spec();
        
		//launch create task
        ManifestMapping.Manifest manifest=this.manifestComposer.composeBoshManifest(spec);
		String deploymentName="on-demande-"+serviceInstance.getServiceInstanceId();
		String textManifest=this.manifestParser.generate(manifest);
		int taskId=this.boshClient.asyncDeploy(deploymentName, textManifest);
		
		//keep task id for future last operation lookup
		serviceInstance.setLastTaskId(taskId);

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
        
        
		
		
		
		//this is the polling from cf expecting async provisionning to transition to finished or error state
		
		Integer taskId=serviceInstance.getLastTaskId();
		if (taskId==null){
			logger.error("unknown last task id for service instance {}",serviceInstanceId);
			return new GetLastServiceOperationResponse().withOperationState(OperationState.FAILED);
		}
		
		//FIXME: if director acces technically KO, return in progress ?
		
		//now get task from director
		Task t=this.boshClient.getTask(taskId);
		switch (t.state){
		case done : new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);break; 
		case error: new GetLastServiceOperationResponse().withOperationState(OperationState.FAILED);break;
		case processing: new GetLastServiceOperationResponse().withOperationState(OperationState.IN_PROGRESS);break;
		case queued: new GetLastServiceOperationResponse().withOperationState(OperationState.IN_PROGRESS);break;
		default: logger.error("unknow task status {}");
		}
		
		return new GetLastServiceOperationResponse().withOperationState(OperationState.IN_PROGRESS);
	}

}